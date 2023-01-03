package ft.app.matcha.domain.block.model;

import java.time.LocalDateTime;

import ft.app.matcha.domain.block.Block;
import ft.app.matcha.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockDto {
	
	private User user;
	private LocalDateTime createdAt;
	
	public static BlockDto fromPeer(Block block) {
		return new BlockDto(block.getPeer(), block.getCreatedAt());
	}
	
}