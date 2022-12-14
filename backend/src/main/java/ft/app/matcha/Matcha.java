package ft.app.matcha;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import ft.app.matcha.configuration.AuthConfigurationProperties;
import ft.app.matcha.configuration.DatabaseConfigurationProperties;
import ft.app.matcha.configuration.EmailConfigurationProperties;
import ft.app.matcha.configuration.MatchaConfigurationProperties;
import ft.app.matcha.domain.auth.AuthController;
import ft.app.matcha.domain.auth.AuthService;
import ft.app.matcha.domain.auth.EmailSender;
import ft.app.matcha.domain.auth.JwtService;
import ft.app.matcha.domain.auth.Token;
import ft.app.matcha.domain.auth.TokenRepository;
import ft.app.matcha.domain.auth.TokenService;
import ft.app.matcha.domain.block.Block;
import ft.app.matcha.domain.block.BlockController;
import ft.app.matcha.domain.block.BlockRepository;
import ft.app.matcha.domain.block.BlockService;
import ft.app.matcha.domain.like.Like;
import ft.app.matcha.domain.like.LikeController;
import ft.app.matcha.domain.like.LikeRepository;
import ft.app.matcha.domain.like.LikeService;
import ft.app.matcha.domain.message.Message;
import ft.app.matcha.domain.message.MessageController;
import ft.app.matcha.domain.message.MessageRepository;
import ft.app.matcha.domain.message.MessageService;
import ft.app.matcha.domain.notification.Notification;
import ft.app.matcha.domain.notification.NotificationController;
import ft.app.matcha.domain.notification.NotificationRepository;
import ft.app.matcha.domain.notification.NotificationService;
import ft.app.matcha.domain.picture.Picture;
import ft.app.matcha.domain.picture.PictureController;
import ft.app.matcha.domain.picture.PictureRepository;
import ft.app.matcha.domain.picture.PictureService;
import ft.app.matcha.domain.report.Report;
import ft.app.matcha.domain.report.ReportController;
import ft.app.matcha.domain.report.ReportRepository;
import ft.app.matcha.domain.report.ReportService;
import ft.app.matcha.domain.socket.WebSocketService;
import ft.app.matcha.domain.tag.Tag;
import ft.app.matcha.domain.tag.TagController;
import ft.app.matcha.domain.tag.TagRepository;
import ft.app.matcha.domain.tag.TagService;
import ft.app.matcha.domain.tag.UserTag;
import ft.app.matcha.domain.tag.UserTagController;
import ft.app.matcha.domain.tag.UserTagRepository;
import ft.app.matcha.domain.tag.UserTagService;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserController;
import ft.app.matcha.domain.user.UserRepository;
import ft.app.matcha.domain.user.UserService;
import ft.app.matcha.security.JwtAuthenticationFilter;
import ft.app.matcha.security.JwtAuthenticator;
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
import ft.framework.mvc.resolver.argument.impl.FormDataHandlerMethodArgumentResolver;
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
import ft.framework.websocket.WebSocketHandler;
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
			final var matchaConfiguration = propertyBinder.bind(new MatchaConfigurationProperties());
			
			final var ormConfiguration = configureOrm(databaseConfiguration, new Class<?>[] {
				User.class,
				Notification.class,
				Like.class,
				Token.class,
				Picture.class,
				Tag.class,
				UserTag.class,
				Message.class,
				Report.class,
				Block.class,
			});
			
			final var webSocket = WebSocketHandler.create(objectMapper);
			
			final var eventPublisher = new ApplicationEventPublisher();
			final var taskScheduler = new WispTaskScheduler();
			
			final var userRepository = new UserRepository(ormConfiguration.getEntityManager());
			final var notificationRepository = new NotificationRepository(ormConfiguration.getEntityManager());
			final var tokenRepository = new TokenRepository(ormConfiguration.getEntityManager());
			final var pictureRepository = new PictureRepository(ormConfiguration.getEntityManager());
			final var likeRepository = new LikeRepository(ormConfiguration.getEntityManager());
			final var tagRepository = new TagRepository(ormConfiguration.getEntityManager());
			final var userTagRepository = new UserTagRepository(ormConfiguration.getEntityManager());
			final var messageRepository = new MessageRepository(ormConfiguration.getEntityManager());
			final var reportRepository = new ReportRepository(ormConfiguration.getEntityManager());
			final var blockRepository = new BlockRepository(ormConfiguration.getEntityManager());
			
			final var emailSender = new EmailSender(emailConfiguration);
			
			final var userService = new UserService(userRepository, matchaConfiguration);
			final var jwtService = new JwtService(userRepository, authConfiguration);
			final var tokenService = new TokenService(tokenRepository, authConfiguration, eventPublisher);
			final var authService = new AuthService(tokenService, userService, jwtService, emailSender, eventPublisher);
			final var pictureService = new PictureService(pictureRepository, matchaConfiguration);
			final var blockService = new BlockService(blockRepository, eventPublisher);
			final var likeService = new LikeService(likeRepository, eventPublisher, blockService);
			final var tagService = new TagService(tagRepository);
			final var userTagService = new UserTagService(userTagRepository, matchaConfiguration);
			final var messageService = new MessageService(messageRepository, eventPublisher);
			final var jwtAuthenticator = new JwtAuthenticator(jwtService);
			final var webSocketService = new WebSocketService(webSocket, jwtAuthenticator);
			final var reportService = new ReportService(reportRepository, eventPublisher);
			final var notificationService = new NotificationService(notificationRepository, blockService, eventPublisher);
			
			final var services = Arrays.asList(new Object[] {
				userService,
				jwtService,
				authService,
				notificationService,
				pictureService,
				likeService,
				tagService,
				userTagService,
				messageService,
				webSocketService,
				reportService,
				blockService,
			});
			
			final var eventListenerFactory = new EventListenerFactory(eventPublisher);
			services.forEach(eventListenerFactory::scan);
			
			final var scheduledFactory = new ScheduledFactory(taskScheduler);
			services.forEach(scheduledFactory::scan);
			
			final var mvcConfiguration = configureMvc(objectMapper, validator, convertionService, jwtAuthenticator);
			final var routeRegistry = new RouteRegistry(mvcConfiguration);
			
			routeRegistry.add(new AuthController(authService));
			routeRegistry.add(new PictureController(userService, pictureService));
			routeRegistry.add(new UserController(userService, eventPublisher));
			routeRegistry.add(new LikeController(likeService, userService));
			routeRegistry.add(new TagController(tagService, userTagService));
			routeRegistry.add(new UserTagController(userTagService, userService, tagService));
			routeRegistry.add(new MessageController(messageService, userService));
			routeRegistry.add(new NotificationController(notificationService));
			routeRegistry.add(new ReportController(reportService, userService));
			routeRegistry.add(new BlockController(blockService, userService));
			
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
	public static MvcConfiguration configureMvc(ObjectMapper objectMapper, Validator validator, ConvertionService conversionService, JwtAuthenticator jwtAuthenticator) {
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
				new PageableHandlerMethodArgumentResolver(validator),
				new AuthenticationHandlerMethodArgumentResolver(),
				new PrincipalHandlerMethodArgumentResolver(),
				new FormDataHandlerMethodArgumentResolver(),
				new BodyHandlerMethodArgumentResolver(objectMapper)))
			.filter(new JwtAuthenticationFilter(jwtAuthenticator))
			.filter(new LoggingFilter())
			.build();
	}
	
}