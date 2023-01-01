package ft.app.matcha.domain.socket.model;

import lombok.Data;

@Data
public class AuthenticatePayload {
	
	private String accessToken;
	
}