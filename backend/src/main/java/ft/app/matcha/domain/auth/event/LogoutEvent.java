package ft.app.matcha.domain.auth.event;

import ft.app.matcha.domain.auth.Token;
import lombok.Getter;

@Getter
@SuppressWarnings("serial")
public class LogoutEvent extends AuthEvent {
	
	private final Token token;
	
	public LogoutEvent(Object source, Token token) {
		super(source);
		
		this.token = token;
	}
	
}