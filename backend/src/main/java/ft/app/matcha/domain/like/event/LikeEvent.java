package ft.app.matcha.domain.like.event;

import ft.app.matcha.domain.like.Like;
import ft.framework.event.ApplicationEvent;
import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class LikeEvent extends ApplicationEvent {
	
	private final Like like;
	
	public LikeEvent(Object source, Like like) {
		super(source);
		
		this.like = like;
	}
	
}