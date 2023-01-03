package ft.app.matcha.domain.auth;

import java.io.StringWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import ft.app.matcha.configuration.EmailConfigurationProperties;
import ft.framework.util.MediaTypes;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EmailSender {
	
	public static final Pattern TITLE_PATTERN = Pattern.compile("<title>(.+?)</title>");
	
	private final EmailConfigurationProperties configuration;
	private final InternetAddress from;
	private final Configuration freemarkerConfiguration;
	
	@SneakyThrows
	public EmailSender(EmailConfigurationProperties configuration) {
		this.configuration = configuration;
		this.from = new InternetAddress(configuration.getSender());
		
		this.freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_29);
		this.freemarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/");
		this.freemarkerConfiguration.setDefaultEncoding("UTF-8");
		this.freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		this.freemarkerConfiguration.setLogTemplateExceptions(false);
		this.freemarkerConfiguration.setWrapUncheckedExceptions(true);
		this.freemarkerConfiguration.setFallbackOnNullLoopVariable(false);
	}
	
	public boolean sendConfirmationEmail(Token token) {
		token.assertType(Token.Type.EMAIL);
		
		final var user = token.getUser();
		final var plain = Objects.requireNonNull(token.getPlain(), "emailToken.plain is null");
		
		final var confirmUrl = "http://localhost:3000/auth/confirm?token=%s".formatted(plain);
		
		final var properties = Map.of(
			"confirmUrl", confirmUrl,
			"firstName", user.getFirstName(),
			"expireAt", token.getExpireAt().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL))
		);
		
		return sendEmail("confirm", user.getEmail(), properties);
	}
	
	public boolean sendEmail(String templateName, String email, Map<?, ?> properties) {
		final var template = getTemplate(templateName);
		final var rendered = render(template, properties);
		
		return sendEmail(rendered, email);
	}
	
	public boolean sendEmail(String html, String email) {
		try {
			final var session = getSession();
			final var message = new MimeMessage(session);
			
			message.setFrom(from);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject(extractSubject(html).orElse(configuration.getDefaultSubject()));
			message.setContent(html, MediaTypes.HTML);
			
			Transport.send(message);
		} catch (MessagingException exception) {
			log.error("Could not send email", exception);
			return false;
		}
		
		return false;
	}
	
	@SneakyThrows
	public String render(Template template, Map<?, ?> properties) {
		final var writter = new StringWriter();
		
		template.process(properties, writter);
		
		return writter.getBuffer().toString();
	}
	
	@SneakyThrows
	public Template getTemplate(String name) {
		return freemarkerConfiguration.getTemplate("/email/%s.ftlh".formatted(name));
	}
	
	public Session getSession() {
		final var properties = System.getProperties();
		properties.put("mail.smtp.host", configuration.getHost());
		properties.put("mail.smtp.port", configuration.getPort());
		properties.put("mail.smtp.ssl.enable", configuration.isSsl());
		properties.put("mail.smtp.auth", configuration.isAuth());
		properties.put("mail.debug", configuration.isDebug());
		
		if (StringUtils.isNotEmpty(configuration.getAuthEmail())) {
			return Session.getInstance(properties, new javax.mail.Authenticator() {
				
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(configuration.getAuthEmail(), configuration.getAuthPassword());
				}
				
			});
		}
		
		return Session.getInstance(properties, null);
	}
	
	public static Optional<String> extractSubject(String html) {
		final var matcher = TITLE_PATTERN.matcher(html);
		
		if (!matcher.find()) {
			return Optional.empty();
		}
		
		return Optional.of(matcher.group(1));
	}
	
}