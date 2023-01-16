package ft.app.matcha.domain.heartbeat;

import java.net.InetAddress;
import java.time.LocalDateTime;

import ft.app.matcha.domain.user.User;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HeartbeatService {
	
	private final HeartbeatRepository repository;
	private final IPLocationService locationService;
	
	public Heartbeat log(User user, InetAddress inetAddress) {
		final var heartbeat = new Heartbeat()
			.setUser(user)
			.setIp(inetAddress.toString())
			.setCreatedAt(LocalDateTime.now());
		
		locationService.resolve(inetAddress).ifPresent((location) -> {
			heartbeat
				.setLatitude(location.latitude())
				.setLongitude(location.longitude())
				.setCity(location.city())
				.setCountry(location.country());
		});
		
		return repository.save(heartbeat);
	}
	
	public Page<Heartbeat> findAll(User user, Pageable pageable) {
		return repository.findAllByUser(user, pageable);
	}
	
}