package ft.app.matcha.domain.like.exception;

import org.eclipse.jetty.http.HttpStatus;

import ft.framework.mvc.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT_409)
@SuppressWarnings("serial")
public class CannotLikeYourselfException extends RuntimeException {
	
	public CannotLikeYourselfException() {
		super("you cannot like yourself");
	}
	
}