package ft.app.matcha.web;

import ft.app.matcha.domain.relationship.Relationship;
import ft.app.matcha.domain.relationship.RelationshipService;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserService;
import ft.app.matcha.domain.user.exception.UserNotFoundException;
import ft.app.matcha.web.dto.UserDto;
import ft.app.matcha.web.form.LikeForm;
import ft.app.matcha.web.map.UserMapper;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Body;
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
@RequestMapping(path = "/users/@me")
@RequiredArgsConstructor
@Authenticated
public class BlockController {
	
	private final RelationshipService relationshipService;
	private final UserService userService;
	private final UserMapper userMapper;
	
	@GetMapping(path = "blocks")
	@ApiOperation(summary = "List users that you blocked.")
	public Page<UserDto> list(
		Pageable pageable,
		@Principal User currentUser
	) {
		final var user = currentUser;
		
		return relationshipService.findAllByUser(user, Relationship.Type.BLOCK, pageable)
			.map((relationship) -> userMapper.toDto(relationship.getPeer(), currentUser));
	}
	
	@PostMapping(path = "blocks")
	@ApiOperation(summary = "Block a peer.")
	public UserDto like(
		@Body LikeForm form,
		@Principal User currentUser
	) {
		final var user = currentUser;
		final var peer = getUser(form.getPeerId());
		final var block = relationshipService.block(user, peer);
		
		return userMapper.toDto(block.getPeer(), currentUser);
	}
	
	@Authenticated
	@DeleteMapping(path = "blocks/{peerId}")
	@ApiOperation(summary = "Unblock a peer.")
	public void unlike(
		@Variable long peerId,
		@Principal User currentUser
	) {
		final var user = currentUser;
		final var peer = getUser(peerId);
		
		relationshipService.unblock(user, peer);
	}
	
	public User getUser(long id) {
		return userService.find(id)
			.orElseThrow(() -> new UserNotFoundException(id));
	}
	
}