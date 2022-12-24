package ft.app.matcha.domain.like;

import java.time.LocalDateTime;

import ft.app.matcha.domain.like.event.LikeEvent;
import ft.app.matcha.domain.like.event.UnlikeEvent;
import ft.app.matcha.domain.like.model.LikeStatus;
import ft.app.matcha.domain.user.User;
import ft.framework.event.ApplicationEventPublisher;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LikeService {
	
	private final LikeRepository repository;
	private final ApplicationEventPublisher eventPublisher;
	
	public Page<Like> findAllWhoLiked(User user, Pageable pageable) {
		return repository.findAllByPeer(user, pageable);
	}
	
	public LikeStatus getStatus(User user, User peer) {
		return repository.findByUserAndPeer(user, peer)
			.map(LikeStatus::from)
			.orElseGet(LikeStatus::none);
	}
	
	public Like like(User user, User peer) {
		final var like = repository.findByUserAndPeer(user, peer)
			.orElseGet(() -> repository.save(
				new Like()
					.setUser(user)
					.setPeer(peer)
					.setCreatedAt(LocalDateTime.now())
			));
		
		eventPublisher.publishEvent(new LikeEvent(this, like));
		
		return like;
	}
	
	public boolean unlike(User user, User peer) {
		final var unliked = repository.deleteByUserAndPeer(user, peer) != 0;
		
		if (unliked) {
			eventPublisher.publishEvent(new UnlikeEvent(this, user, peer));
		}
		
		return unliked;
	}
	
}