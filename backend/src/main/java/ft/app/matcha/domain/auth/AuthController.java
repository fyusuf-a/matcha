package ft.app.matcha.domain.auth;

import ft.app.matcha.domain.auth.model.ConfirmForm;
import ft.app.matcha.domain.auth.model.ForgotForm;
import ft.app.matcha.domain.auth.model.LoginForm;
import ft.app.matcha.domain.auth.model.LogoutForm;
import ft.app.matcha.domain.auth.model.RefreshForm;
import ft.app.matcha.domain.auth.model.RegisterForm;
import ft.app.matcha.domain.auth.model.ResetPasswordForm;
import ft.app.matcha.domain.auth.model.Tokens;
import ft.app.matcha.domain.user.User;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Body;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.PostMapping;
import ft.framework.mvc.annotation.Principal;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.swagger.annotation.ApiOperation;
import ft.framework.validation.annotation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/auth")
public class AuthController {
	
	private final AuthService authService;
	
	@GetMapping(path = "/self")
	@Authenticated
	@ApiOperation(summary = "Show authenticated principal.")
	public User self(
		@Principal User principal
	) {
		return principal;
	}
	
	@PostMapping(path = "/login")
	@ApiOperation(summary = "Login with an email and a password.")
	public Tokens login(
		@Body @Valid LoginForm form
	) {
		return authService.login(form);
	}
	
	@PostMapping(path = "/register")
	@ApiOperation(summary = "Create an account.")
	public Tokens register(
		@Body @Valid RegisterForm form
	) {
		return authService.register(form);
	}
	
	@PostMapping(path = "/refresh")
	@ApiOperation(summary = "Refresh a JWT token using a refresh-token.")
	public Tokens refresh(
		@Body @Valid RefreshForm form
	) {
		return authService.refresh(form);
	}
	
	@PostMapping(path = "/logout")
	@ApiOperation(summary = "Logout and invalidate a refresh-token.")
	public void logout(
		@Body @Valid LogoutForm form
	) {
		authService.logout(form);
	}
	
	@PostMapping(path = "/confirm")
	@ApiOperation(summary = "Confirm an account.")
	public void confirm(
		@Body @Valid ConfirmForm form
	) {
		authService.confirm(form);
	}
	
	@PostMapping(path = "/forgot")
	@ApiOperation(summary = "Send a 'forgot password' email.")
	public void forgot(
		@Body @Valid ForgotForm form
	) {
		authService.forgot(form);
	}
	
	@PostMapping(path = "/reset-password")
	public void resetPassword(
		@Body @Valid ResetPasswordForm form
	) {
		authService.resetPassword(form);
	}
	
}