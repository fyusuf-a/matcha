package ft.app.matcha.web.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import ft.app.matcha.domain.like.Like;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeStatus {
	
	private boolean liked;
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime likedAt;
	
	public static LikeStatus from(Like like) {
		return new LikeStatus(true, like.getCreatedAt());
	}
	
	public static LikeStatus none() {
		return new LikeStatus();
	}
	
}