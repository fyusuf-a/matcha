package ft.app.matcha.web;

import ft.app.matcha.domain.tag.Tag;
import ft.app.matcha.domain.tag.TagService;
import ft.app.matcha.domain.tag.UserTag;
import ft.app.matcha.domain.tag.UserTagService;
import ft.app.matcha.domain.tag.exception.TagNotFoundException;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserService;
import ft.app.matcha.domain.user.exception.OnlyYourselfException;
import ft.app.matcha.domain.user.exception.UserNotFoundException;
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
@RequestMapping(path = "/users/{id}/tags")
@RequiredArgsConstructor
public class UserTagController {
	
	private final UserTagService userTagService;
	private final UserService userService;
	private final TagService tagService;
	
	@GetMapping
	@ApiOperation(summary = "List user's tags.")
	public Page<Tag> list(
		Pageable pageable,
		@Variable long id
	) {
		final var user = getUser(id, null);
		
		return userTagService.findAll(user, pageable);
	}
	
	@Authenticated
	@PostMapping(path = "{tagId}")
	@ApiOperation(summary = "Add a tag to the user's tags.")
	public UserTag add(
		@Variable long id,
		@Variable long tagId,
		@Principal User currentUser
	) {
		final var user = getUser(id, currentUser);
		final var tag = getTag(tagId);
		
		return userTagService.add(user, tag);
	}
	
	@Authenticated
	@DeleteMapping(path = "{tagId}")
	@ApiOperation(summary = "Remove a tag from the user's tags.")
	public boolean remove(
		@Variable long id,
		@Variable long tagId,
		@Principal User currentUser
	) {
		final var user = getUser(id, currentUser);
		final var tag = getTag(tagId);
		
		return userTagService.remove(user, tag);
	}
	
	public User getUser(long id, User currentUser) {
		if (currentUser != null) {
			if (currentUser.getId() == id) {
				return currentUser;
			}
			
			throw new OnlyYourselfException();
		}
		
		return userService.find(id)
			.orElseThrow(() -> new UserNotFoundException(id));
	}
	
	public Tag getTag(long id) {
		return tagService.find(id)
			.orElseThrow(() -> new TagNotFoundException(id));
	}
	
}