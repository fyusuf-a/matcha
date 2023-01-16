package ft.app.matcha.domain.heartbeat;

import ft.app.matcha.domain.user.User;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.orm.EntityManager;
import ft.framework.orm.repository.Repository;

public class HeartbeatRepository extends Repository<Heartbeat, Long> {
	
	public HeartbeatRepository(EntityManager entityManager) {
		super(entityManager, Heartbeat.class);
	}
	
	public Page<Heartbeat> findAllByUser(User user, Pageable pageable) {
		final var predicate = builder.and(
			builder.equals(Heartbeat.Fields.user, user)
		);
		
		return findAllBy(predicate, pageable);
	}
	
}