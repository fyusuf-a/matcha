package ft.app.matcha.domain.heartbeat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import ft.app.matcha.domain.heartbeat.Presence.Availability;

public record Presence(
	Availability availability,
	@JsonFormat(shape = JsonFormat.Shape.STRING) LocalDateTime since) {
	
	public static final Presence UNKNOWN = new Presence(Availability.UNAVAILABLE, null);
	
	public enum Availability {
		
		UNAVAILABLE,
		AVAILABLE;
	
	}
	
}