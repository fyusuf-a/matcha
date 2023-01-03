package ft.app.matcha.domain.user;

import java.util.Optional;

import ft.app.matcha.configuration.MatchaConfigurationProperties;
import ft.app.matcha.domain.block.event.BlockedEvent;
import ft.app.matcha.domain.like.event.LikedEvent;
import ft.app.matcha.domain.like.event.UnlikedEvent;
import ft.app.matcha.domain.report.event.ReportedEvent;
import ft.app.matcha.domain.user.exception.LoginAlreadyTakenException;
import ft.app.matcha.domain.user.model.UserPatchForm;
import ft.framework.event.annotation.EventListener;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.orm.error.DuplicateValueException;

public class UserService {
	
	private final UserRepository repository;
	private final FameValues fameValues;
	
	public UserService(UserRepository repository, MatchaConfigurationProperties properties) {
		this.repository = repository;
		
		this.fameValues = new FameValues(
			properties.getFameOnLike(),
			properties.getFameOnUnlike(),
			properties.getFameOnBlock(),
			properties.getFameOnReport()
		);
	}
	
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
	
	@EventListener
	public void onLiked(LikedEvent event) {
		final var user = event.getLike().getPeer();
		
		changeFame(user, fameValues.onLike());
	}
	
	@EventListener
	public void onUnliked(UnlikedEvent event) {
		final var user = event.getPeer();
		
		changeFame(user, fameValues.onUnlike());
	}
	
	@EventListener
	public void onBlocked(BlockedEvent event) {
		final var user = event.getBlock().getPeer();
		
		changeFame(user, fameValues.onBlock());
	}
	
	@EventListener
	public void onReported(ReportedEvent event) {
		final var user = event.getReport().getUser();
		
		changeFame(user, fameValues.onReport());
	}
	
	public User changeFame(User user, int change) {
		return repository.save(user.setFame(user.getFame() + change));
	}
	
	static record FameValues(
		int onLike,
		int onUnlike,
		int onBlock,
		int onReport) {
	}
	
}