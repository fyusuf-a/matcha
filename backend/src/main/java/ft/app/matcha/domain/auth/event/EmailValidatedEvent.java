package ft.app.matcha.domain.auth.event;

import ft.app.matcha.domain.user.User;
import lombok.Getter;

@Getter
@SuppressWarnings("serial")
public class EmailValidatedEvent extends AuthEvent {
	
	private final User user;
	
	public EmailValidatedEvent(Object source, User user) {
		super(source);
		
		this.user = user;
	}
	
}