package ft.app.matcha.domain.block.event;

import ft.app.matcha.domain.block.Block;
import ft.framework.event.ApplicationEvent;
import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class BlockEvent extends ApplicationEvent {
	
	private final Block block;
	
	public BlockEvent(Object source, Block block) {
		super(source);
		
		this.block = block;
	}
	
}