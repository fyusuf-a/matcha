package ft.app.matcha.domain.socket;

import ft.app.matcha.domain.user.User;

public class RoomFormatter {
	
	public static String toUser(User user) {
		return "user-%s".formatted(user.getId());
	}
	
}