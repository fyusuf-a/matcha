package ft.app.matcha.domain.auth;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;

import ft.app.matcha.configuration.AuthConfigurationProperties;
import ft.app.matcha.domain.auth.event.TokenValidatedEvent;
import ft.app.matcha.domain.user.User;
import ft.framework.event.ApplicationEventPublisher;
import ft.framework.schedule.annotation.Scheduled;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenService {
	
	private final TokenRepository repository;
	private final Map<Token.Type, TokenConfiguration> configurations;
	private final ApplicationEventPublisher eventPublisher;
	
	public TokenService(TokenRepository repository, AuthConfigurationProperties properties, ApplicationEventPublisher eventPublisher) {
		this.repository = repository;
		this.configurations = createTokenConfigurations(properties);
		this.eventPublisher = eventPublisher;
	}
	
	public Token create(Token.Type type, User user) {
		if (type.isUnique()) {
			repository.deleteAllByUser(user);
		}
		
		final var configuration = configurations.get(type);
		
		final var plain = RandomStringUtils.randomAlphanumeric(configuration.getLength());
		final var encoded = encode(plain);
		
		final var createdAt = LocalDateTime.now();
		final var expiredAt = createdAt.plus(configuration.getExpiration());
		
		return repository.save(new Token()
			.setType(type)
			.setUser(user)
			.setEncoded(encoded)
			.setPlain(plain)
			.setCreatedAt(createdAt)
			.setExpireAt(expiredAt)
		);
	}
	
	public Optional<Token> find(Token.Type type, String plain) {
		final var encoded = encode(plain);
		
		return repository.findByTypeAndEncoded(type, encoded);
	}
	
	public void validate(Token token) {
		eventPublisher.publishEvent(new TokenValidatedEvent(this, token));
		repository.delete(token);
	}
	
	public long deleteAllByUser(User user) {
		return repository.deleteAllByUser(user);
	}
	
	@Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
	public long deleteExpired() {
		return repository.deleteAllByExpireAtLessThan(LocalDateTime.now());
	}
	
	public static String encode(String plain) {
		return PasswordEncoder.encode(plain);
	}
	
	public static Map<Token.Type, TokenConfiguration> createTokenConfigurations(AuthConfigurationProperties properties) {
		return Map.ofEntries(
			Map.entry(Token.Type.EMAIL, new TokenConfiguration(properties.getEmailTokenLength(), properties.getEmailTokenExpiration())),
			Map.entry(Token.Type.REFRESH, new TokenConfiguration(properties.getRefreshTokenLength(), properties.getRefreshTokenExpiration())),
			Map.entry(Token.Type.PASSWORD, new TokenConfiguration(properties.getPasswordTokenLength(), properties.getPasswordTokenExpiration()))
		);
	}
	
}