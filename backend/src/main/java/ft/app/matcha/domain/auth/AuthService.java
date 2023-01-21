package ft.app.matcha.domain.auth;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import ft.app.matcha.domain.auth.event.RegisterEvent;
import ft.app.matcha.domain.auth.exception.InvalidPasswordException;
import ft.app.matcha.domain.auth.exception.InvalidTokenException;
import ft.app.matcha.domain.auth.exception.WrongLoginOrPasswordException;
import ft.app.matcha.domain.picture.PictureService;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserService;
import ft.framework.event.ApplicationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthService {
	
	private final TokenService tokenService;
	private final UserService userService;
	private final JwtService jwtService;
	private final EmailSender emailSender;
	private final OAuthService oAuthService;
	private final PictureService pictureService;
	private final ApplicationEventPublisher eventPublisher;
	
	public Tokens login(String login, String password) {
		final var encoded = encode(password);
		
		return userService.find(login, encoded)
			.map(this::createTokens)
			.orElseThrow(WrongLoginOrPasswordException::new);
	}
	
	public Tokens register(String firstName, String lastName, String email, String login, String password) {
		final var encoded = encode(password);
		final var user = userService.create(firstName, lastName, email, login, encoded, false);
		
		eventPublisher.publishEvent(new RegisterEvent(this, user));
		
		final var token = tokenService.create(Token.Type.EMAIL, user);
		emailSender.sendConfirmationEmail(token);
		
		return createTokens(user);
	}
	
	public Tokens refresh(String refreshToken) {
		return tokenService.validate(Token.Type.REFRESH, refreshToken)
			.map(this::createTokens)
			.orElseThrow(() -> new InvalidTokenException(Token.Type.REFRESH));
	}
	
	public void logout(String refreshToken) {
		tokenService.validate(Token.Type.REFRESH, refreshToken)
			.orElseThrow(() -> new InvalidTokenException(Token.Type.REFRESH));
	}
	
	public void confirm(String confirmToken) {
		final var user = tokenService.validate(Token.Type.EMAIL, confirmToken)
			.orElseThrow(() -> new InvalidTokenException(Token.Type.EMAIL));
		
		userService.save(user
			.setEmailConfirmed(true)
			.setEmailConfirmedAt(LocalDateTime.now())
		);
	}
	
	public void forgot(String email) {
		userService.find(email)
			.map((user) -> tokenService.create(Token.Type.PASSWORD, user))
			.ifPresent(emailSender::sendPasswordResetEmail);
	}
	
	public void changePassword(User user, String oldPassword, String newPassword) {
		final var oldEncoded = encode(oldPassword);
		if (!oldEncoded.equals(user.getPassword())) {
			throw new InvalidPasswordException();
		}
		
		final var encoded = encode(newPassword);
		userService.save(user.setPassword(encoded));
	}
	
	public void resetPassword(String passwordToken, String password) {
		final var user = tokenService.validate(Token.Type.PASSWORD, passwordToken)
			.orElseThrow(() -> new InvalidTokenException(Token.Type.PASSWORD));
		
		final var encoded = encode(password);
		userService.save(user.setPassword(encoded));
	}
	
	public String getOAuthUrl() {
		return oAuthService.getAuthorizeUrl();
	}
	
	public Tokens validateOAuthCode(String code) {
		final var idToken = oAuthService.getIdToken(code);
		final var oauthUser = oAuthService.getUser(idToken);
		
		final var optional = userService.find(oauthUser.email());
		if (optional.isPresent()) {
			return createTokens(optional.get());
		}
		
		final var login = oauthUser.email().split("@")[0];
		final var user = userService.create(oauthUser.firstName(), oauthUser.lastName(), oauthUser.email(), login, null, true);
		
		final var pictureUrl = oauthUser.pictureUrl();
		if (StringUtils.isNotBlank(pictureUrl)) {
			try {
				final var picture = pictureService.upload(user, pictureUrl);
				pictureService.setDefault(picture);
			} catch (Exception exception) {
				log.warn("Could not download oauth picture", exception);
			}
		}
		
		eventPublisher.publishEvent(new RegisterEvent(this, user));
		
		return createTokens(user);
	}
	
	private Tokens createTokens(User user) {
		final var token = tokenService.create(Token.Type.REFRESH, user);
		final var accessToken = jwtService.generate(token.getUser());
		
		return new Tokens(user, accessToken, token.getPlain());
	}
	
	public static String encode(String password) {
		return PasswordEncoder.encode(password);
	}
	
}