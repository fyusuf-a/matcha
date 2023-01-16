package ft.app.matcha.domain.heartbeat;

public record Location(
	String country,
	String city,
	Double latitude,
	Double longitude) {
	
}