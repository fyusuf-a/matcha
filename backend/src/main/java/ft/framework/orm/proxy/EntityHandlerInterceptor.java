package ft.framework.orm.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

public class EntityHandlerInterceptor {
	
	public static final List<String> EXCLUDED = Arrays.asList(
		"toString",
		"hashCode",
		"equals");
	
	@RuntimeType
	public static Object intercept(
		@This Object self,
		@FieldValue(ProxiedEntity.HANDLER_FIELD) EntityHandler handler,
		@AllArguments Object[] arguments,
		@Origin Method method
	) throws Exception {
		final var methodName = method.getName();
		
		if (!handler.isInitialized()) {
			handler.fetchLazy();
		}
		
		if (methodName.startsWith("set")) {
			final var fieldName = method.getName().substring(3);
			
			if (!handler.markModified(fieldName)) {
				handler.markAllModified();
			}
		} else if (!methodName.startsWith("get") && !methodName.startsWith("is") && !EXCLUDED.contains(methodName)) {
			handler.markAllModified();
		}
		
		final var returnValue = method.invoke(handler.getOriginal(), arguments);
		if (returnValue == handler.getOriginal()) {
			return self;
		}
		
		return returnValue;
	}
	
}