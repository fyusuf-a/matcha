package ft.app.matcha.domain.auth;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;

import ft.app.matcha.domain.auth.event.EmailValidatedEvent;
import ft.app.matcha.domain.auth.event.RegisterEvent;
import ft.app.matcha.domain.user.User;
import ft.framework.event.ApplicationEventPublisher;
import ft.framework.event.annotation.EventListener;
import ft.framework.schedule.annotation.Scheduled;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailTokenService {
	
	public static final int LENGTH = 128;
	public static final Duration EXPIRATION = Duration.ofDays(1);
	
	private final EmailTokenRepository repository;
	private final EmailSender emailSender;
	private final ApplicationEventPublisher eventPublisher;
	
	public EmailToken create(User user) {
		repository.deleteAllByUser(user);
		
		final var plain = RandomStringUtils.randomAlphanumeric(LENGTH);
		final var encoded = encode(plain);
		
		final var createdAt = LocalDateTime.now();
		final var expiredAt = createdAt.plus(EXPIRATION);
		
		return repository.save(new EmailToken()
			.setUser(user)
			.setEncoded(encoded)
			.setPlain(plain)
			.setCreatedAt(createdAt)
			.setExpireAt(expiredAt)
		);
	}
	
	public boolean validate(String plain) {
		final var encoded = encode(plain);
		final var token = repository.findByEncoded(encoded);
		
		token.ifPresent((token_) -> {
			final var user = token_.getUser();
			
			eventPublisher.publishEvent(new EmailValidatedEvent(this, user));
			
			repository.delete(token_);
		});
		
		return token.isPresent();
	}
	
	@Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
	public long deleteExpired() {
		return repository.deleteAllByExpireAtLessThan(LocalDateTime.now());
	}
	
	@EventListener
	public void onRegister(RegisterEvent event) {
		final var user = event.getUser();
		final var token = create(user);
		
		emailSender.sendConfirmationEmail(token);
	}
	
	public static String encode(String plain) {
		return PasswordEncoder.encode(plain);
	}
	
}