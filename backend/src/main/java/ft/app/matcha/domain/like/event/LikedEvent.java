package ft.app.matcha.domain.like.event;

import ft.app.matcha.domain.like.Like;
import ft.framework.event.ApplicationEvent;
import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class LikedEvent extends ApplicationEvent {
	
	private final Like like;
	private final boolean cross;
	
	public LikedEvent(Object source, Like like, boolean cross) {
		super(source);
		
		this.like = like;
		this.cross = cross;
	}
	
}