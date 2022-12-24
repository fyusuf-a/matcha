package ft.app.matcha.domain.like.event;

import ft.app.matcha.domain.user.User;
import ft.framework.event.ApplicationEvent;
import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class UnlikeEvent extends ApplicationEvent {
	
	private final User user;
	private final User peer;
	
	public UnlikeEvent(Object source, User user, User peer) {
		super(source);
		
		this.user = user;
		this.peer = peer;
	}
	
}