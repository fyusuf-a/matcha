package ft.app.matcha.domain.like;

import java.time.LocalDateTime;

import ft.app.matcha.domain.block.BlockService;
import ft.app.matcha.domain.block.event.BlockEvent;
import ft.app.matcha.domain.like.event.LikeEvent;
import ft.app.matcha.domain.like.event.UnlikeEvent;
import ft.app.matcha.domain.like.exception.CannotLikeBlockedUserException;
import ft.app.matcha.domain.like.exception.CannotLikeYourselfException;
import ft.app.matcha.domain.like.model.LikeStatus;
import ft.app.matcha.domain.user.User;
import ft.framework.event.ApplicationEventPublisher;
import ft.framework.event.annotation.EventListener;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LikeService {
	
	private final LikeRepository repository;
	private final ApplicationEventPublisher eventPublisher;
	private final BlockService blockService;
	
	public Page<Like> findAllWhoLiked(User user, Pageable pageable) {
		return repository.findAllByPeer(user, pageable);
	}
	
	public LikeStatus getStatus(User user, User peer) {
		return repository.findByUserAndPeer(user, peer)
			.map(LikeStatus::from)
			.orElseGet(LikeStatus::none);
	}
	
	public Like like(User user, User peer) {
		if (user.getId() == peer.getId()) {
			throw new CannotLikeYourselfException();
		}
		
		if (blockService.getStatus(user, peer).isBlocked()) {
			throw new CannotLikeBlockedUserException();
		}
		
		final var optional = repository.findByUserAndPeer(user, peer);
		if (optional.isPresent()) {
			return optional.get();
		}
		
		final var like = repository.save(
			new Like()
				.setUser(user)
				.setPeer(peer)
				.setCreatedAt(LocalDateTime.now())
		);
		
		final var cross = repository.existsByUserAndPeer(peer, user);
		
		eventPublisher.publishEvent(new LikeEvent(this, like, cross));
		
		return like;
	}
	
	public boolean unlike(User user, User peer) {
		final var unliked = repository.deleteByUserAndPeer(user, peer) != 0;
		
		if (unliked) {
			eventPublisher.publishEvent(new UnlikeEvent(this, user, peer));
		}
		
		return unliked;
	}
	
	@EventListener
	public void onBlock(BlockEvent event) {
		final var block = event.getBlock();
		
		repository.deleteByUserAndPeer(block.getUser(), block.getPeer());
	}
	
}