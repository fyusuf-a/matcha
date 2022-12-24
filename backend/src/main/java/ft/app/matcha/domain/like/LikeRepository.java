package ft.app.matcha.domain.like;

import java.util.Optional;

import ft.app.matcha.domain.user.User;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.orm.EntityManager;
import ft.framework.orm.predicate.Predicate;
import ft.framework.orm.repository.Repository;

public class LikeRepository extends Repository<Like, Long> {
	
	public LikeRepository(EntityManager entityManager) {
		super(entityManager, Like.class);
	}
	
	public Optional<Like> findByUserAndPeer(User user, User peer) {
		return findBy(createByUserAndPeerPredicate(user, peer));
	}
	
	public long deleteByUserAndPeer(User user, User peer) {
		return deleteAllBy(createByUserAndPeerPredicate(user, peer));
	}
	
	public Page<Like> findAllByPeer(User peer, Pageable pageable) {
		return findAllBy(builder.and(
			builder.equals(Like.Fields.peer, peer)
		), pageable);
	}
	
	public Predicate<Like> createByUserAndPeerPredicate(User user, User peer) {
		return builder.and(
			builder.equals(Like.Fields.user, user),
			builder.equals(Like.Fields.peer, peer)
		);
	}
	
}