package ft.app.matcha.security;

import ft.app.matcha.domain.auth.JwtService;
import ft.framework.mvc.security.Authentication;
import lombok.RequiredArgsConstructor;
import spark.utils.StringUtils;

@RequiredArgsConstructor
public class JwtAuthenticator {
	
	private final JwtService jwtService;
	
	public Authentication authenticate(String authorization) {
		if (StringUtils.isBlank(authorization)) {
			return null;
		}
		
		final var jwt = getJwt(authorization);
		if (StringUtils.isBlank(jwt)) {
			return null;
		}
		
		return authenticateToken(jwt);
	}
	
	public Authentication authenticateToken(String token) {
		if (StringUtils.isBlank(token)) {
			return null;
		}
		
		final var user = jwtService.decode(token);
		if (user == null) {
			return null;
		}
		
		return new UserAuthentication(user);
	}
	
	public String getJwt(String authorization) {
		final var parts = authorization.split(" ", 2);
		
		if (parts.length != 2) {
			return null;
		}
		
		return parts[1];
	}
	
}