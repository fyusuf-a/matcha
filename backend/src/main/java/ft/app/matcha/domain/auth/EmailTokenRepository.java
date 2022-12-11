package ft.app.matcha.domain.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import ft.app.matcha.domain.user.User;
import ft.framework.orm.EntityManager;
import ft.framework.orm.repository.Repository;

public class EmailTokenRepository extends Repository<EmailToken, Long> {
	
	public EmailTokenRepository(EntityManager entityManager) {
		super(entityManager, EmailToken.class);
	}
	
	public Optional<EmailToken> findByEncoded(String encrypted) {
		return findBy(
			builder.equals(EmailToken.Fields.encoded, encrypted)
		);
	}
	
	public long deleteAllByExpireAtLessThan(LocalDateTime dateTime) {
		return deleteAllBy(
			builder.lessThan(EmailToken.Fields.expireAt, dateTime)
		);
	}
	
	public long deleteAllByUser(User user) {
		return deleteAllBy(
			builder.equals(EmailToken.Fields.user, user)
		);
	}
	
}