package ft.app.matcha.domain.auth;

import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenConfiguration {

	private final int length;
	private final Duration expiration;
	
}