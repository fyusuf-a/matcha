package ft.app.matcha.domain.notification;

import org.apache.commons.lang3.tuple.Pair;

import ft.app.matcha.domain.like.Like;
import ft.app.matcha.domain.message.Message;
import ft.app.matcha.domain.user.User;

public class NotificationFormatter {
	
	public static Pair<String, String> formatLiked(Like like) {
		final var user = like.getUser();
		
		return Pair.of(
			"%s liked you!".formatted(user.getFirstName()),
			toUserLink(user)
		);
	}

	public static Pair<String, String> formatUnliked(User user, User peer) {
		return Pair.of(
			"%s unliked you!".formatted(user.getFirstName()),
			toUserLink(user)
		);
	}

	public static Pair<String, String> formatMessageReceived(Message message) {
		final var user = message.getUser();
		
		return Pair.of(
			"%s sent you a message!".formatted(user.getFirstName()),
			"%s/messages".formatted(toUserLink(user))
		);
	}
	
	public static String toUserLink(User user) {
		return "/users/%s".formatted(user.getId());
	}
	
}