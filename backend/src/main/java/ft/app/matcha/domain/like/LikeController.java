package ft.app.matcha.domain.like;

import ft.app.matcha.domain.like.model.LikeDto;
import ft.app.matcha.domain.like.model.LikeStatus;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserService;
import ft.app.matcha.domain.user.exception.UserNotFoundException;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.DeleteMapping;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.PostMapping;
import ft.framework.mvc.annotation.Principal;
import ft.framework.mvc.annotation.Query;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.annotation.Variable;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.swagger.annotation.ApiOperation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = "/likes")
@RequiredArgsConstructor
public class LikeController {
	
	private final LikeService likeService;
	private final UserService userService;
	
	@GetMapping
	@ApiOperation(summary = "List likes given by a peer.")
	public Page<LikeDto> list(
		Pageable pageable,
		@Query long peerId
	) {
		final var peer = getUser(peerId);
		
		return likeService.findAllWhoLiked(peer, pageable)
			.map(LikeDto::fromUser);
	}
	
	@Authenticated
	@GetMapping(path = "{peerId}")
	@ApiOperation(summary = "Show the like status with a peer.")
	public LikeStatus show(
		@Variable long peerId,
		@Principal User currentUser
	) {
		final var peer = getUser(peerId);
		
		return likeService.getStatus(currentUser, peer);
	}
	
	@Authenticated
	@PostMapping(path = "{peerId}")
	@ApiOperation(summary = "Like a peer.")
	public LikeStatus like(
		@Variable long peerId,
		@Principal User currentUser
	) {
		final var peer = getUser(peerId);
		final var like = likeService.like(currentUser, peer);
		
		return LikeStatus.from(like);
	}
	
	@Authenticated
	@DeleteMapping(path = "{peerId}")
	@ApiOperation(summary = "Unlike a peer.")
	public LikeStatus unlike(
		@Variable long peerId,
		@Principal User currentUser
	) {
		final var peer = getUser(peerId);
		likeService.unlike(currentUser, peer);
		
		return LikeStatus.none();
	}
	
	public User getUser(long id) {
		return userService.find(id)
			.orElseThrow(() -> new UserNotFoundException(id));
	}
	
}