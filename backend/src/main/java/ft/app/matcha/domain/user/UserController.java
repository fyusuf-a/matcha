package ft.app.matcha.domain.user;

import ft.app.matcha.domain.user.event.UserViewedEvent;
import ft.app.matcha.domain.user.exception.OnlyYourselfException;
import ft.app.matcha.domain.user.exception.UserNotFoundException;
import ft.app.matcha.domain.user.model.UserPatchForm;
import ft.framework.event.ApplicationEventPublisher;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Body;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.PatchMapping;
import ft.framework.mvc.annotation.PostMapping;
import ft.framework.mvc.annotation.Principal;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.annotation.Variable;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.swagger.annotation.ApiOperation;
import ft.framework.validation.annotation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/users")
public class UserController {
	
	private final UserService userService;
	private final ApplicationEventPublisher eventPublisher;
	
	@GetMapping
	@ApiOperation(summary = "List users.")
	public Page<User> index(Pageable pageable) {
		return userService.findAll(pageable);
	}
	
	@GetMapping(path = "{id}")
	@ApiOperation(summary = "List an user.")
	public User show(
		@Variable long id
	) {
		return userService.find(id)
			.orElseThrow(() -> new UserNotFoundException(id));
	}
	
	@Authenticated
	@PostMapping(path = "{id}/view")
	@ApiOperation(summary = "Trigger a 'view' notification.")
	public void view(
		@Variable long id,
		@Principal User currentUser
	) {
		final var user = show(id);
		
		if (user.getId() != currentUser.getId()) {
			eventPublisher.publishEvent(new UserViewedEvent(this, user, currentUser));
		}
	}
	
	@Authenticated
	@PatchMapping(path = "{id}")
	@ApiOperation(summary = "Update an user.")
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