package ft.app.matcha.domain.message;

import java.time.LocalDateTime;

import ft.app.matcha.domain.message.event.MessageCreatedEvent;
import ft.app.matcha.domain.user.User;
import ft.framework.event.ApplicationEventPublisher;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageService {
	
	private final MessageRepository repository;
	private final ApplicationEventPublisher eventPublisher;
	
	public Page<Message> findAll(User user1, User user2, Pageable pageable) {
		return repository.findAllBetweenTwoUser(user1, user2, pageable);
	}
	
	public Message create(User user, User peer, String content) {
		final var message = repository.save(
			new Message()
				.setUser(user)
				.setPeer(peer)
				.setContent(content)
				.setCreatedAt(LocalDateTime.now())
		);
		
		eventPublisher.publishEvent(new MessageCreatedEvent(this, message));
		
		return message;
	}
	
//	@Scheduled(fixedDelay = 1000)
	public void fake() {
		create(new User().setId(1), new User().setId(2), "Hey its %s!".formatted(System.currentTimeMillis()));
	}
	
}