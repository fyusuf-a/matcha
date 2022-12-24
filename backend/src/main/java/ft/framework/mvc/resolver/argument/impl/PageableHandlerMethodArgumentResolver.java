package ft.framework.mvc.resolver.argument.impl;

import java.lang.reflect.Parameter;
import java.util.Set;

import ft.framework.mvc.domain.Pageable;
import ft.framework.mvc.resolver.argument.HandlerMethodArgumentResolver;
import ft.framework.validation.ValidationException;
import ft.framework.validation.Validator;
import ft.framework.validation.constraint.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

@RequiredArgsConstructor
public class PageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
	
	public static final long DEFAULT_SIZE = 20;
	public static final long DEFAULT_PAGE = 0;
	public static final String QUERY_PAGE = "page";
	public static final String QUERY_SIZE = "size";
	
	private final Validator validator;
	
	@Override
	public boolean supportsParameter(Parameter method) {
		return Pageable.class.equals(method.getType());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object resolveArgument(Parameter parameter, Request request, Response response) throws Exception {
		final var page = getOrDefault(request, QUERY_PAGE, DEFAULT_PAGE);
		final var size = getOrDefault(request, QUERY_SIZE, DEFAULT_SIZE);
		
		final var pageable = new Pageable(size, page);
		
		final var violations = validator.validate(pageable);
		if (!violations.isEmpty()) {
			throw new ValidationException((Set<ConstraintViolation<?>>) (Object) violations);
		}
		
		return pageable;
	}
	
	public long getOrDefault(Request request, String query, long defaultValue) {
		final var value = request.queryParams(query);
		
		if (StringUtils.isNotBlank(value)) {
			return Long.parseLong(value);
		}
		
		return defaultValue;
	}
	
}