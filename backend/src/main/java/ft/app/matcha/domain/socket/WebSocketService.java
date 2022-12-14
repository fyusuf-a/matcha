package ft.app.matcha.domain.socket;

import ft.app.matcha.domain.message.event.MessageCreatedEvent;
import ft.app.matcha.domain.notification.event.NotificationCreatedEvent;
import ft.app.matcha.domain.socket.model.AuthenticatePayload;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.security.JwtAuthenticator;
import ft.framework.event.annotation.EventListener;
import ft.framework.websocket.WebSocketHandler;

public class WebSocketService {
	
	public static final String AUTHENTICATE_EVENT = "authenticate";
	public static final String MESSAGE_CREATED_EVENT = "message.created";
	public static final String NOTIFICATION_CREATED_EVENT = "notification.created";
	
	private final WebSocketHandler webSocket;
	
	public WebSocketService(WebSocketHandler webSocket, JwtAuthenticator jwtAuthenticator) {
		this.webSocket = webSocket;
		
		webSocket.addPacketListener(AUTHENTICATE_EVENT, AuthenticatePayload.class, (connection, payload) -> {
			final var authentication = jwtAuthenticator.authenticateToken(payload.getAccessToken());
			
			if (authentication != null) {
				connection.setAuthentication(authentication);
				
				if (authentication.getPrincipal() instanceof User user) {
					connection.join(RoomFormatter.toUser(user));
				}
			}
			
			connection.emit(AUTHENTICATE_EVENT, authentication.getPrincipal());
		});
	}
	
	@EventListener
	public void onMessageCreated(MessageCreatedEvent event) {
		final var message = event.getMessage();
		
		sendToUser(message.getUser(), MESSAGE_CREATED_EVENT, message);
		sendToUser(message.getPeer(), MESSAGE_CREATED_EVENT, message);
	}
	
	@EventListener
	public void onNotificationCreated(NotificationCreatedEvent event) {
		final var notification = event.getNotification();
		
		sendToUser(notification.getUser(), NOTIFICATION_CREATED_EVENT, notification);
	}
	
	public <T> void sendToUser(User user, String event, T payload) {
		final var room = RoomFormatter.toUser(user);
		
		webSocket.emit(room, event, payload);
	}
	
}