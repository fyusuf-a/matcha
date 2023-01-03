package ft.app.matcha.domain.auth;

import java.util.Optional;

import ft.app.matcha.domain.user.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshTokenService {
	
	private final TokenService tokenService;
	
	public Token create(User user) {
		return tokenService.create(Token.Type.REFRESH, user);
	}
	
	public Optional<Token> refresh(String plain) {
		final var previous = find(plain);
		
		if (previous.isEmpty()) {
			return Optional.empty();
		}
		
		final var user = previous.get().getUser();
		
		tokenService.validate(previous.get());
		
		return Optional.of(create(user));
	}
	
	public Optional<Token> find(String plain) {
		return tokenService.find(Token.Type.REFRESH, plain);
	}
	
	public void delete(Token token) {
		token.assertType(Token.Type.REFRESH);
		
		tokenService.validate(token);
	}
	
}