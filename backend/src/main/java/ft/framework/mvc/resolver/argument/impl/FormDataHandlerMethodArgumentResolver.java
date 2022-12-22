package ft.framework.mvc.resolver.argument.impl;

import java.lang.reflect.Parameter;

import javax.servlet.MultipartConfigElement;

import org.apache.commons.lang3.StringUtils;

import ft.framework.mvc.annotation.FormData;
import ft.framework.mvc.resolver.argument.HandlerMethodArgumentResolver;
import spark.Request;
import spark.Response;

public class FormDataHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
	
	public static final String CONFIG_KEY = "org.eclipse.jetty.multipartConfig";
	
	@Override
	public boolean supportsParameter(Parameter parameter) {
		return parameter.getDeclaredAnnotation(FormData.class) != null;
	}
	
	@Override
	public Object resolveArgument(Parameter parameter, Request request, Response response) throws Exception {
		final var annotation = parameter.getDeclaredAnnotation(FormData.class);
		
		var name = annotation.name();
		if (StringUtils.isEmpty(name)) {
			name = parameter.getName();
		}
		
		// TODO Move elsewhere
		MultipartConfigElement config = (MultipartConfigElement) request.raw().getAttribute(CONFIG_KEY);
		if (config == null) {
			config = new MultipartConfigElement("/tmp");
			request.raw().setAttribute(CONFIG_KEY, config);
		}
		
		final var value = request.raw().getPart(name);
		if (value == null) {
			throw new Exception(String.format("missing `%s`", name));
		}
		
		return value;
	}
	
}