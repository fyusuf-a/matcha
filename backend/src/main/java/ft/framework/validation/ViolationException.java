package ft.framework.validation;

import java.util.Set;

import ft.framework.validation.constraint.ConstraintViolation;
import lombok.Getter;

@SuppressWarnings({ "serial" })
public class ViolationException extends RuntimeException {
	
	@Getter
	private final Set<ConstraintViolation<?>> violations;
	
	@SuppressWarnings("unchecked")
	public <T> ViolationException(Set<ConstraintViolation<T>> violations) {
		super("validation");
		
		// TODO
		this.violations = (Set<ConstraintViolation<?>>) (Object) violations;
	}
	
}