package ft.app.matcha.web.dto;

import java.time.LocalDateTime;

import ft.app.matcha.domain.like.Like;
import ft.app.matcha.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
	
	private User user;
	private LocalDateTime createdAt;
	
	public static LikeDto fromUser(Like like) {
		return new LikeDto(like.getUser(), like.getCreatedAt());
	}
	
	public static LikeDto fromPeer(Like like) {
		return new LikeDto(like.getPeer(), like.getCreatedAt());
	}
	
}