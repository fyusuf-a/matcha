package ft.app.matcha;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import ft.app.matcha.configuration.AuthConfigurationProperties;
import ft.app.matcha.configuration.DatabaseConfigurationProperties;
import ft.app.matcha.configuration.EmailConfigurationProperties;
import ft.app.matcha.domain.auth.AuthController;
import ft.app.matcha.domain.auth.AuthService;
import ft.app.matcha.domain.auth.EmailSender;
import ft.app.matcha.domain.auth.EmailToken;
import ft.app.matcha.domain.auth.EmailTokenRepository;
import ft.app.matcha.domain.auth.EmailTokenService;
import ft.app.matcha.domain.auth.JwtService;
import ft.app.matcha.domain.auth.RefreshToken;
import ft.app.matcha.domain.auth.RefreshTokenRepository;
import ft.app.matcha.domain.auth.RefreshTokenService;
import ft.app.matcha.domain.like.Like;
import ft.app.matcha.domain.notification.Notification;
import ft.app.matcha.domain.notification.NotificationRepository;
import ft.app.matcha.domain.notification.NotificationService;
import ft.app.matcha.domain.picture.PictureController;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserController;
import ft.app.matcha.domain.user.UserRepository;
import ft.app.matcha.domain.user.UserService;
import ft.app.matcha.security.JwtAuthenticationFilter;
import ft.framework.convert.service.ConvertionService;
import ft.framework.convert.service.SimpleConvertionService;
import ft.framework.event.ApplicationEventPublisher;
import ft.framework.event.listener.EventListenerFactory;
import ft.framework.mvc.MvcConfiguration;
import ft.framework.mvc.http.convert.SimpleHttpMessageConversionService;
import ft.framework.mvc.http.convert.impl.InputStreamHttpMessageConverter;
import ft.framework.mvc.http.convert.impl.JacksonHttpMessageConverter;
import ft.framework.mvc.mapping.RouteRegistry;
import ft.framework.mvc.resolver.argument.impl.AuthenticationHandlerMethodArgumentResolver;
import ft.framework.mvc.resolver.argument.impl.BodyHandlerMethodArgumentResolver;
import ft.framework.mvc.resolver.argument.impl.PageableHandlerMethodArgumentResolver;
import ft.framework.mvc.resolver.argument.impl.PrincipalHandlerMethodArgumentResolver;
import ft.framework.mvc.resolver.argument.impl.QueryHandlerMethodArgumentResolver;
import ft.framework.mvc.resolver.argument.impl.RequestHandlerMethodArgumentResolver;
import ft.framework.mvc.resolver.argument.impl.ResponseHandlerMethodArgumentResolver;
import ft.framework.mvc.resolver.argument.impl.VariableHandlerMethodArgumentResolver;
import ft.framework.orm.EntityManager;
import ft.framework.orm.OrmConfiguration;
import ft.framework.orm.dialect.MySQLDialect;
import ft.framework.orm.mapping.MappingBuilder;
import ft.framework.property.PropertyBinder;
import ft.framework.property.resolver.EnvironmentPropertyResolver;
import ft.framework.schedule.ScheduledFactory;
import ft.framework.schedule.impl.WispTaskScheduler;
import ft.framework.swagger.SwaggerBuilder;
import ft.framework.swagger.controller.SwaggerController;
import ft.framework.trace.filter.LoggingFilter;
import ft.framework.validation.ValidationException;
import ft.framework.validation.Validator;
import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Matcha {
	
	@SneakyThrows
	public static void main(String[] args) {
		try {
			Dotenv.configure()
				.systemProperties()
				.ignoreIfMissing()
				.load();
			
			final var objectMapper = new ObjectMapper()
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.registerModule(new JavaTimeModule());
			
			final var validator = new Validator();
			final var convertionService = new SimpleConvertionService();
			
			final var propertyBinder = PropertyBinder.builder()
				.validator(validator)
				.convertionService(convertionService)
				.resolver(new EnvironmentPropertyResolver())
				.build();
			
			final var databaseConfiguration = propertyBinder.bind(new DatabaseConfigurationProperties());
			final var authConfiguration = propertyBinder.bind(new AuthConfigurationProperties());
			final var emailConfiguration = propertyBinder.bind(new EmailConfigurationProperties());
			
			final var ormConfiguration = configureOrm(databaseConfiguration, new Class<?>[] {
				User.class,
				RefreshToken.class,
				Notification.class,
				Like.class,
				EmailToken.class,
			});
			
			final var eventPublisher = new ApplicationEventPublisher();
			final var taskScheduler = new WispTaskScheduler();
			
			final var userRepository = new UserRepository(ormConfiguration.getEntityManager());
			final var refreshTokenRepository = new RefreshTokenRepository(ormConfiguration.getEntityManager());
			final var notificationRepository = new NotificationRepository(ormConfiguration.getEntityManager());
			final var emailTokenRepository = new EmailTokenRepository(ormConfiguration.getEntityManager());
			
			final var emailSender = new EmailSender(emailConfiguration);
			
			final var userService = new UserService(userRepository);
			final var jwtService = new JwtService(userRepository, authConfiguration);
			final var refreshTokenService = new RefreshTokenService(refreshTokenRepository, authConfiguration);
			final var emailTokenService = new EmailTokenService(emailTokenRepository, authConfiguration, emailSender, eventPublisher);
			final var authService = new AuthService(userService, refreshTokenService, emailTokenService, jwtService, eventPublisher);
			final var notificationService = new NotificationService(notificationRepository);
			
			final var services = Arrays.asList(new Object[] {
				userService,
				jwtService,
				refreshTokenService,
				authService,
				notificationService,
				emailTokenService,
			});
			
			final var eventListenerFactory = new EventListenerFactory(eventPublisher);
			services.forEach(eventListenerFactory::scan);
			
			final var scheduledFactory = new ScheduledFactory(taskScheduler);
			services.forEach(scheduledFactory::scan);
			
			final var mvcConfiguration = configureMvc(objectMapper, validator, convertionService, jwtService);
			final var routeRegistry = new RouteRegistry(mvcConfiguration);
			
			routeRegistry.add(new AuthController(authService));
			routeRegistry.add(new PictureController());
			routeRegistry.add(new UserController(userRepository));
			
			final var swagger = new OpenAPI()
				.schemaRequirement("JWT", new SecurityScheme()
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT"))
				.info(new Info()
					.title("Matcha")
					.version("1.0"));
			
			SwaggerBuilder.build(swagger, routeRegistry.getRoutes());
			routeRegistry.add(new SwaggerController(swagger));
			
			routeRegistry.markReady();
		} catch (ValidationException exception) {
			log.error("A model could not be validated", exception);
			
			final var violations = exception.getViolations();
			for (final var violation : violations) {
				log.error("{}: {}", violation.getPropertyPath(), violation.getMessage());
			}
			
			System.exit(1);
		} catch (Exception exception) {
			log.error("Could start the server", exception);
			System.exit(1);
		}
	}
	
	@SneakyThrows
	public static OrmConfiguration configureOrm(DatabaseConfigurationProperties databaseConfiguration, Class<?>... entityClasses) {
		final var dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setServerName(databaseConfiguration.getHost());
		dataSource.setPort(databaseConfiguration.getPort());
		dataSource.setUser(databaseConfiguration.getUser());
		dataSource.setPassword(databaseConfiguration.getPassword());
		dataSource.setDatabaseName(databaseConfiguration.getDatabase());
		dataSource.setAutoReconnect(databaseConfiguration.isAutoReconnect());
		dataSource.setAutoReconnectForPools(databaseConfiguration.isAutoReconnect());
		
		final var dialect = new MySQLDialect();
		
		final var mappingBuilder = new MappingBuilder();
		Arrays.stream(entityClasses)
			.forEach(mappingBuilder::analyze);
		
		final var entityManager = new EntityManager(dataSource, dialect, mappingBuilder);
		entityManager.applyDataDefinitionLanguage();
		
		return OrmConfiguration.builder()
			.entityManager(entityManager)
			.build();
	}
	
	@SneakyThrows
	public static MvcConfiguration configureMvc(ObjectMapper objectMapper, Validator validator, ConvertionService conversionService, JwtService jwtService) {
		log.info("Waking up");
		
		return MvcConfiguration.builder()
			.objectMapper(objectMapper)
			.validator(validator)
			.conversionService(conversionService)
			.httpMessageConversionService(
				SimpleHttpMessageConversionService.builder()
					.converter(new InputStreamHttpMessageConverter())
					.converter(new JacksonHttpMessageConverter(objectMapper, true))
					.build())
			.methodArgumentResolvers(Arrays.asList(
				new VariableHandlerMethodArgumentResolver(),
				new QueryHandlerMethodArgumentResolver(),
				new RequestHandlerMethodArgumentResolver(),
				new ResponseHandlerMethodArgumentResolver(),
				new PageableHandlerMethodArgumentResolver(),
				new AuthenticationHandlerMethodArgumentResolver(),
				new PrincipalHandlerMethodArgumentResolver(),
				new BodyHandlerMethodArgumentResolver(objectMapper)))
			.filter(new JwtAuthenticationFilter(jwtService))
			.filter(new LoggingFilter())
			.build();
	}
	
}