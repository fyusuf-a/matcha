package ft.app.matcha.domain.user;

import ft.app.matcha.domain.user.exception.UserNotFoundException;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.annotation.Variable;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/users")
public class UserController {
	
	private final UserRepository repository;
	
	@GetMapping
	public Page<User> index(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	@GetMapping(path = "{id}")
	public User show(
		@Variable long id
	) {
		return repository.findById(id)
			.orElseThrow(() -> new UserNotFoundException(id));
	}
	
}