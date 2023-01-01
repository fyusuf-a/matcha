package ft.framework.websocket.packet;

import java.util.function.BiConsumer;

import ft.framework.websocket.SocketConnection;

@FunctionalInterface
public interface PacketListener<T> extends BiConsumer<SocketConnection, T> {
	
}