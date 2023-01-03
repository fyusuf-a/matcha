package ft.app.matcha.domain.user;

import java.util.Optional;

import ft.app.matcha.domain.user.exception.LoginAlreadyTakenException;
import ft.app.matcha.domain.user.model.UserPatchForm;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.orm.error.DuplicateValueException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository repository;
	
	public User create(String firstName, String lastName, String email, String login, String password) {
		try {
			return repository.save(new User()
				.setFirstName(firstName)
				.setLastName(lastName)
				.setEmail(email)
				.setLogin(login)
				.setPassword(password)
			);
		} catch (DuplicateValueException exception) {
			throw new LoginAlreadyTakenException(login);
		}
	}
	
	public Page<User> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public Optional<User> find(Long id) {
		return repository.findById(id);
	}

	public Optional<User> find(String email) {
		return repository.findByEmail(email);
	}
	
	public Optional<User> find(String login, String password) {
		return repository.findByLoginAndPassword(login, password);
	}
	
	public User save(User user) {
		return repository.save(user);
	}

	public User patch(User user, UserPatchForm form) {
		Optional.ofNullable(form.getFirstName()).ifPresent(user::setFirstName);
		Optional.ofNullable(form.getLastName()).ifPresent(user::setLastName);
		Optional.ofNullable(form.getBiography()).ifPresent(user::setBiography);
		Optional.ofNullable(form.getGender()).ifPresent(user::setGender);
		Optional.ofNullable(form.getSexualOrientation()).ifPresent(user::setSexualOrientation);
		
		return repository.save(user);
	}
	
}