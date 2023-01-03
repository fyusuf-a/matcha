package ft.app.matcha.domain.block;

import java.time.LocalDateTime;

import ft.app.matcha.domain.block.event.BlockedEvent;
import ft.app.matcha.domain.block.exception.CannotBlockYourselfException;
import ft.app.matcha.domain.block.model.BlockStatus;
import ft.app.matcha.domain.report.event.ReportedEvent;
import ft.app.matcha.domain.user.User;
import ft.framework.event.ApplicationEventPublisher;
import ft.framework.event.annotation.EventListener;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BlockService {
	
	private final BlockRepository repository;
	private final ApplicationEventPublisher eventPublisher;
	
	public Page<Block> findAll(User user, Pageable pageable) {
		return repository.findAllByUser(user, pageable);
	}
	
	public BlockStatus getStatus(User user, User peer) {
		return repository.findByUserAndPeer(user, peer)
			.map(BlockStatus::from)
			.orElseGet(BlockStatus::none);
	}
	
	public Block block(User user, User peer) {
		if (user.getId() == peer.getId()) {
			throw new CannotBlockYourselfException();
		}
		
		final var optional = repository.findByUserAndPeer(user, peer);
		if (optional.isPresent()) {
			return optional.get();
		}
		
		final var block = repository.save(
			new Block()
				.setUser(user)
				.setPeer(peer)
				.setCreatedAt(LocalDateTime.now())
		);
		
		eventPublisher.publishEvent(new BlockedEvent(this, block));
		
		return block;
	}
	
	public boolean unblock(User user, User peer) {
		return repository.deleteByUserAndPeer(user, peer) != 0;
	}
	
	public boolean isBlocked(User user, User peer) {
		return repository.existsByUserAndPeer(user, peer);
	}
	
	@EventListener
	public void onReported(ReportedEvent event) {
		final var report = event.getReport();
		
		final var user = report.getReporter();
		final var peer = report.getUser();
		
		block(user, peer);
	}
	
}