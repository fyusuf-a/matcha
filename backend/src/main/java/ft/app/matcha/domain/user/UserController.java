package ft.app.matcha.domain.user;

import ft.app.matcha.domain.user.exception.OnlyYourselfException;
import ft.app.matcha.domain.user.exception.UserNotFoundException;
import ft.app.matcha.domain.user.model.UserPatchForm;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Body;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.PatchMapping;
import ft.framework.mvc.annotation.Principal;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.annotation.Variable;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.validation.annotation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/users")
public class UserController {
	
	private final UserService userService;
	
	@GetMapping
	public Page<User> index(Pageable pageable) {
		return userService.findAll(pageable);
	}
	
	@GetMapping(path = "{id}")
	public User show(
		@Variable long id
	) {
		return userService.find(id)
			.orElseThrow(() -> new UserNotFoundException(id));
	}
	
	@Authenticated
	@PatchMapping(path = "{id}")
	public User patch(
		@Variable long id,
		@Valid @Body UserPatchForm form,
		@Principal User user
	) {
		if (id != user.getId()) {
			throw new OnlyYourselfException();
		}
		
		return userService.patch(user, form);
	}
	
}