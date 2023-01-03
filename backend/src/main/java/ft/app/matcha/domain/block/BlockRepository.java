package ft.app.matcha.domain.block;

import java.util.Optional;

import ft.app.matcha.domain.user.User;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.orm.EntityManager;
import ft.framework.orm.predicate.Predicate;
import ft.framework.orm.repository.Repository;

public class BlockRepository extends Repository<Block, Long> {
	
	public BlockRepository(EntityManager entityManager) {
		super(entityManager, Block.class);
	}
	
	public Optional<Block> findByUserAndPeer(User user, User peer) {
		return findBy(createByUserAndPeerPredicate(user, peer));
	}
	
	public long deleteByUserAndPeer(User user, User peer) {
		return deleteAllBy(createByUserAndPeerPredicate(user, peer));
	}
	
	public Page<Block> findAllByUser(User user, Pageable pageable) {
		return findAllBy(builder.and(
			builder.equals(Block.Fields.user, user)
		), pageable);
	}
	
	public Predicate<Block> createByUserAndPeerPredicate(User user, User peer) {
		return builder.and(
			builder.equals(Block.Fields.user, user),
			builder.equals(Block.Fields.peer, peer)
		);
	}
	
}