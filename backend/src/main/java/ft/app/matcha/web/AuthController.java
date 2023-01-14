package ft.app.matcha.web;

import ft.app.matcha.domain.auth.AuthService;
import ft.app.matcha.domain.auth.Tokens;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.web.form.ConfirmForm;
import ft.app.matcha.web.form.ForgotForm;
import ft.app.matcha.web.form.LoginForm;
import ft.app.matcha.web.form.LogoutForm;
import ft.app.matcha.web.form.RefreshForm;
import ft.app.matcha.web.form.RegisterForm;
import ft.app.matcha.web.form.ResetPasswordForm;
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
		return authService.login(form.getLogin(), form.getPassword());
	}
	
	@PostMapping(path = "/register")
	@ApiOperation(summary = "Create an account.")
	public Tokens register(
		@Body @Valid RegisterForm form
	) {
		return authService.register(form.getFirstName(), form.getLastName(), form.getEmail(), form.getLogin(), form.getPassword());
	}
	
	@PostMapping(path = "/refresh")
	@ApiOperation(summary = "Refresh a JWT token using a refresh-token.")
	public Tokens refresh(
		@Body @Valid RefreshForm form
	) {
		return authService.refresh(form.getRefreshToken());
	}
	
	@PostMapping(path = "/logout")
	@ApiOperation(summary = "Logout and invalidate a refresh-token.")
	public void logout(
		@Body @Valid LogoutForm form
	) {
		authService.logout(form.getRefreshToken());
	}
	
	@PostMapping(path = "/confirm")
	@ApiOperation(summary = "Confirm an account.")
	public void confirm(
		@Body @Valid ConfirmForm form
	) {
		authService.confirm(form.getToken());
	}
	
	@PostMapping(path = "/forgot")
	@ApiOperation(summary = "Send a 'forgot password' email.")
	public void forgot(
		@Body @Valid ForgotForm form
	) {
		authService.forgot(form.getEmail());
	}
	
	@PostMapping(path = "/reset-password")
	public void resetPassword(
		@Body @Valid ResetPasswordForm form
	) {
		authService.resetPassword(form.getToken(), form.getPassword());
	}
	
}