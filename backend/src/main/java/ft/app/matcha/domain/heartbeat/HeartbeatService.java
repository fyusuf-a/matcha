package ft.app.matcha.domain.heartbeat;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import ft.app.matcha.configuration.HeartbeatConfigurationProperties;
import ft.app.matcha.domain.user.User;

public class HeartbeatService {
	
	private final HeartbeatRepository repository;
	private final IPLocationService locationService;
	private final Duration availabilityTimeout;
	
	public HeartbeatService(HeartbeatRepository repository, IPLocationService locationService, HeartbeatConfigurationProperties properties) {
		this.repository = repository;
		this.locationService = locationService;
		this.availabilityTimeout = properties.getAvailabilityTimeout();
	}
	
	public Heartbeat log(User user, InetAddress inetAddress) {
		final var resolved = locationService.resolve(inetAddress);
		
		final var heartbeat = repository.findByUser(user)
			.orElseGet(() -> new Heartbeat()
				.setUser(user)
				.setCreatedAt(LocalDateTime.now()))
			.setIp(inetAddress.toString())
			.setUpdatedAt(LocalDateTime.now());
		
		resolved.ifPresent((location) -> {
			heartbeat
				.setLatitude(location.latitude())
				.setLongitude(location.longitude())
				.setCity(location.city())
				.setCountry(location.country());
		});
		
		return repository.save(heartbeat);
	}
	
	public Presence getPresence(User user) {
		return repository.findByUser(user)
			.map((heartbeat) -> new Presence(
				toPresenceAvailability(heartbeat.getUpdatedAt()),
				heartbeat.getUpdatedAt()
			))
			.orElse(Presence.UNKNOWN);
	}
	
	public Presence.Availability toPresenceAvailability(LocalDateTime lastBeat) {
		if (LocalDateTime.now().isBefore(lastBeat.plus(availabilityTimeout))) {
			return Presence.Availability.AVAILABLE;
		}
		
		return Presence.Availability.UNAVAILABLE;
	}
	
}