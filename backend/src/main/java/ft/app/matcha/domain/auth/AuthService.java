package ft.app.matcha.domain.auth;

import java.time.LocalDateTime;

import ft.app.matcha.domain.auth.event.RegisterEvent;
import ft.app.matcha.domain.auth.exception.InvalidTokenException;
import ft.app.matcha.domain.auth.exception.WrongLoginOrPasswordException;
import ft.app.matcha.domain.auth.model.ChangePasswordForm;
import ft.app.matcha.domain.auth.model.ConfirmForm;
import ft.app.matcha.domain.auth.model.ForgotForm;
import ft.app.matcha.domain.auth.model.LoginForm;
import ft.app.matcha.domain.auth.model.LogoutForm;
import ft.app.matcha.domain.auth.model.RefreshForm;
import ft.app.matcha.domain.auth.model.RegisterForm;
import ft.app.matcha.domain.auth.model.Tokens;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserService;
import ft.framework.event.ApplicationEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthService {
	
	private final TokenService tokenService;
	private final UserService userService;
	private final JwtService jwtService;
	private final EmailSender emailSender;
	private final ApplicationEventPublisher eventPublisher;
	
	public Tokens login(LoginForm form) {
		final var password = encode(form.getPassword());
		
		return userService.find(form.getLogin(), password)
			.map(this::createTokens)
			.orElseThrow(WrongLoginOrPasswordException::new);
	}
	
	public Tokens register(RegisterForm form) {
		final var password = encode(form.getPassword());
		final var user = userService.create(form.getFirstName(), form.getLastName(), form.getEmail(), form.getLogin(), password);
		
		eventPublisher.publishEvent(new RegisterEvent(this, user));
		
		final var token = tokenService.create(Token.Type.EMAIL, user);
		emailSender.sendConfirmationEmail(token);
		
		return createTokens(user);
	}
	
	public Tokens refresh(RefreshForm form) {
		return tokenService.validate(Token.Type.REFRESH, form.getRefreshToken())
			.map(this::createTokens)
			.orElseThrow(() -> new InvalidTokenException(Token.Type.REFRESH));
	}
	
	public void logout(LogoutForm form) {
		tokenService.validate(Token.Type.REFRESH, form.getRefreshToken())
			.orElseThrow(() -> new InvalidTokenException(Token.Type.REFRESH));
	}
	
	public void confirm(ConfirmForm form) {
		final var user = tokenService.validate(Token.Type.EMAIL, form.getToken())
			.orElseThrow(() -> new InvalidTokenException(Token.Type.EMAIL));
		
		userService.save(user
			.setEmailConfirmed(true)
			.setEmailConfirmedAt(LocalDateTime.now())
		);
	}
	
	public void forgot(ForgotForm form) {
		userService.find(form.getEmail())
			.map((user) -> tokenService.create(Token.Type.PASSWORD, user))
			.ifPresent(emailSender::sendPasswordResetEmail);
	}
	
	public void changePassword(ChangePasswordForm form) {
		final var user = tokenService.validate(Token.Type.PASSWORD, form.getToken())
			.orElseThrow(() -> new InvalidTokenException(Token.Type.PASSWORD));
		
		final var password = encode(form.getPassword());
		userService.save(user.setPassword(password));
	}
	
	private Tokens createTokens(User user) {
		final var token = tokenService.create(Token.Type.REFRESH, user);
		final var accessToken = jwtService.generate(token.getUser());
		
		return new Tokens(accessToken, token.getPlain());
	}
	
	public static String encode(String password) {
		return PasswordEncoder.encode(password);
	}
	
}