package ft.app.matcha.domain.auth.event;

import ft.app.matcha.domain.auth.Token;
import lombok.Getter;

@Getter
@SuppressWarnings("serial")
public class RefreshEvent extends AuthEvent {
	
	private final Token token;
	
	public RefreshEvent(Object source, Token token) {
		super(source);
		
		this.token = token;
	}
	
}