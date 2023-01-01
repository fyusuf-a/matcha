package ft.app.matcha.domain.user.event;

import ft.app.matcha.domain.user.User;
import ft.framework.event.ApplicationEvent;
import lombok.Getter;


@SuppressWarnings("serial")
@Getter
public class UserViewedEvent extends ApplicationEvent {
	
	private final User user;
	private final User viewer;

	public UserViewedEvent(Object source, User user, User viewer) {
		super(source);
		
		this.user = user;
		this.viewer = viewer;
	}
	
}