package ft.app.matcha.domain.auth;

import java.util.Optional;

import ft.app.matcha.domain.auth.event.RegisterEvent;
import ft.app.matcha.domain.user.User;
import ft.framework.event.annotation.EventListener;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailTokenService {
	
	private final TokenService tokenService;
	private final EmailSender emailSender;
	
	public Token create(User user) {
		return tokenService.create(Token.Type.EMAIL, user);
	}
	
	public Optional<User> validate(String plain) {
		final var token = tokenService.find(Token.Type.EMAIL, plain);
		
		if (token.isEmpty()) {
			return Optional.empty();
		}
		
		final var user = token.get().getUser();
		
		tokenService.validate(token.get());
		
		return Optional.of(user);
	}
	
	@EventListener
	public void onRegister(RegisterEvent event) {
		final var user = event.getUser();
		final var token = create(user);
		
		emailSender.sendConfirmationEmail(token);
	}
	
}