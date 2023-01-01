package ft.app.matcha.security;

import ft.framework.mvc.security.Authentication;
import ft.framework.mvc.security.AuthenticationFilter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends AuthenticationFilter {
	
	private final JwtAuthenticator jwtAuthenticator;
	
	@Override
	public Authentication authenticate(String authorization) {
		return jwtAuthenticator.authenticate(authorization);
	}
	
}