package ft.app.matcha.web;

import ft.app.matcha.domain.block.BlockService;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserService;
import ft.app.matcha.domain.user.exception.UserNotFoundException;
import ft.app.matcha.web.dto.BlockDto;
import ft.app.matcha.web.dto.BlockStatus;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.DeleteMapping;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.PostMapping;
import ft.framework.mvc.annotation.Principal;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.annotation.Variable;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.swagger.annotation.ApiOperation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = "/blocks")
@RequiredArgsConstructor
public class BlockController {
	
	private final BlockService blockService;
	private final UserService userService;
	
	@Authenticated
	@GetMapping
	@ApiOperation(summary = "List blocked users.")
	public Page<BlockDto> list(
		Pageable pageable,
		@Principal User currentUser
	) {
		return blockService.findAll(currentUser, pageable)
			.map(BlockDto::fromPeer);
	}
	
	@Authenticated
	@GetMapping(path = "{peerId}")
	@ApiOperation(summary = "See if you blocked an user.")
	public BlockStatus show(
		@Variable long peerId,
		@Principal User currentUser
	) {
		final var peer = getUser(peerId);
		
		return blockService.getStatus(currentUser, peer);
	}
	
	@Authenticated
	@PostMapping(path = "{peerId}")
	@ApiOperation(summary = "Block an user.")
	public BlockStatus block(
		@Variable long peerId,
		@Principal User currentUser
	) {
		final var peer = getUser(peerId);
		final var block = blockService.block(currentUser, peer);
		
		return BlockStatus.from(block);
	}
	
	@Authenticated
	@DeleteMapping(path = "{peerId}")
	@ApiOperation(summary = "Unblock an user.")
	public BlockStatus unblock(
		@Variable long peerId,
		@Principal User currentUser
	) {
		final var peer = getUser(peerId);
		blockService.unblock(currentUser, peer);
		
		return BlockStatus.none();
	}
	
	public User getUser(long id) {
		return userService.find(id)
			.orElseThrow(() -> new UserNotFoundException(id));
	}
	
}