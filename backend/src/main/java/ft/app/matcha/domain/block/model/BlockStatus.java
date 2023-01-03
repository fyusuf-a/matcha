package ft.app.matcha.domain.block.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import ft.app.matcha.domain.block.Block;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockStatus {
	
	private boolean blocked;
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime blockedAt;
	
	public static BlockStatus from(Block block) {
		return new BlockStatus(true, block.getCreatedAt());
	}
	
	public static BlockStatus none() {
		return new BlockStatus();
	}
	
}