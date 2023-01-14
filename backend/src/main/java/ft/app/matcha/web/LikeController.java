package ft.app.matcha.web;

import ft.app.matcha.domain.relationship.Relationship;
import ft.app.matcha.domain.relationship.RelationshipService;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserService;
import ft.app.matcha.domain.user.exception.UserNotFoundException;
import ft.app.matcha.security.UserAuthentication;
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
import ft.framework.mvc.security.Authentication;
import ft.framework.swagger.annotation.ApiOperation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class LikeController {
	
	private final RelationshipService relationshipService;
	private final UserService userService;
	private final UserMapper userMapper;
	
	@GetMapping(path = "{userId}/likers")
	@ApiOperation(summary = "List users that liked this user.")
	public Page<UserDto> listLikers(
		Pageable pageable,
		@Variable long userId,
		Authentication authentication
	) {
		final var principal = UserAuthentication.getUser(authentication);
		final var user = getUser(userId);
		
		return relationshipService.findAllByPeer(user, Relationship.Type.LIKE, pageable)
			.map((relationship) -> userMapper.toDto(relationship.getUser(), principal));
	}
	
	@GetMapping(path = "{userId}/liking")
	@ApiOperation(summary = "List users that this user has liked.")
	public Page<UserDto> list(
		Pageable pageable,
		@Variable long userId,
		Authentication authentication
	) {
		final var principal = UserAuthentication.getUser(authentication);
		final var user = getUser(userId);
		
		return relationshipService.findAllByUser(user, Relationship.Type.LIKE, pageable)
			.map((relationship) -> userMapper.toDto(relationship.getPeer(), principal));
	}
	
	@Authenticated
	@PostMapping(path = "@me/liking")
	@ApiOperation(summary = "Like a peer.")
	public UserDto like(
		@Body LikeForm form,
		@Principal User currentUser
	) {
		final var user = currentUser;
		final var peer = getUser(form.getPeerId());
		final var like = relationshipService.like(user, peer);
		
		return userMapper.toDto(like.getPeer(), currentUser);
	}
	
	@Authenticated
	@DeleteMapping(path = "@me/liking/{peerId}")
	@ApiOperation(summary = "Unlike a peer.")
	public void unlike(
		@Variable long peerId,
		@Principal User currentUser
	) {
		final var user = currentUser;
		final var peer = getUser(peerId);
		
		relationshipService.unlike(user, peer);
	}
	
	public User getUser(long id) {
		return userService.find(id)
			.orElseThrow(() -> new UserNotFoundException(id));
	}
	
}