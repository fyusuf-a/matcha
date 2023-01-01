package ft.framework.websocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.fasterxml.jackson.databind.ObjectMapper;

import ft.framework.websocket.packet.Packet;
import ft.framework.websocket.packet.PacketListener;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import spark.Spark;

@Slf4j
@Data
@WebSocket
public class WebSocketHandler {
	
	private final ObjectMapper objectMapper;
	
	private final Map<Session, SocketConnection> connections = new ConcurrentHashMap<>();
	private final Map<String, Set<SocketConnection>> rooms = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private final Map<String, List<Pair<PacketListener<?>, Class<?>>>> packetListeners = new ConcurrentHashMap<>();
	
	@OnWebSocketConnect
	public void onConnect(Session session) throws Exception {
		log.trace("Session connected: {}", session.getRemoteAddress());
		
		final var connection = new SocketConnection(session, this);
		connections.put(session, connection);
	}
	
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		log.trace("Session closed: {} (statusCode={}, reason={})", session.getRemoteAddress(), statusCode, reason);
		
		final var connection = connections.remove(session);
		if (connection == null) {
			return;
		}
		
		connection.leaveAll();
	}
	
	@SneakyThrows
	@OnWebSocketMessage
	@SuppressWarnings("unchecked")
	public void onMessage(Session session, String message) {
		final var connection = connections.get(session);
		if (connection == null) {
			return;
		}
		
		final var packet = objectMapper.readValue(message, Packet.class);
		final var listeners = packetListeners.getOrDefault(packet.getEvent(), Collections.emptyList());
		
		if (listeners.isEmpty()) {
			log.trace("No listener for event: {}", packet.getEvent());
		} else {
			for (final var pair : listeners) {
				final var listener = (PacketListener<Object>) pair.getLeft();
				final var clazz = pair.getRight();
				
				final var payload = objectMapper.convertValue(packet.getPayload(), clazz);
				listener.accept(connection, payload);
			}
		}
	}
	
	@OnWebSocketError
	public void onError(Session session, Throwable throwable) {
		log.trace("Session error: {} (throwable={})", session.getRemoteAddress(), throwable.getMessage());
		throwable.printStackTrace();
	}
	
	public void emit(String room, String event, Object payload) {
		final var connections = rooms.get(room);
		if (connections == null) {
			return;
		}
		
		final var json = convertToJson(event, payload);
		connections.forEach((connection) -> connection.emit(json));
	}

	@SneakyThrows
	public String convertToJson(String event, Object payload) {
		final var packet = new Packet(event, payload);
		return objectMapper.writeValueAsString(packet);
	}
	
	public <T> void addPacketListener(String event, Class<T> payloadClass, PacketListener<T> listener) {
		packetListeners.computeIfAbsent(event, (key) -> new ArrayList<>())
			.add(Pair.of(listener, payloadClass));
	}
	
	public static WebSocketHandler create(ObjectMapper objectMapper) {
		final var handler = new WebSocketHandler(objectMapper);
		
		final var path = "/websocket";
		Spark.webSocket(path, handler);
		log.info("Exposing {}", path);
		
		return handler;
	}
	
}