package ft.framework.property.resolver;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class EnvironmentPropertyResolver implements PropertyResolver {
	
	@Override
	public Optional<String> resolve(List<String> paths) {
		final var key = toKey(paths);
		
		final var property = System.getProperty(key);
		if (property != null) {
			return Optional.of(property);
		}
		
		final var envVar = System.getenv(key);
		if (envVar != null) {
			return Optional.of(envVar);
		}
		
		return Optional.empty();
	}
	
	public static String toKey(List<String> paths) {
		return paths.stream()
			.flatMap((path) -> Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(path)))
			.map(String::toUpperCase)
			.collect(Collectors.joining("_"));
	}
	
}