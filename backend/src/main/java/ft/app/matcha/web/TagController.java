package ft.app.matcha.web;

import ft.app.matcha.domain.tag.Tag;
import ft.app.matcha.domain.tag.TagService;
import ft.app.matcha.domain.tag.UserTagService;
import ft.app.matcha.domain.tag.exception.TagNotFoundException;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.web.form.TagCreateForm;
import ft.framework.mvc.annotation.Body;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.PostMapping;
import ft.framework.mvc.annotation.Query;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.annotation.Variable;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.swagger.annotation.ApiOperation;
import ft.framework.validation.annotation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = "/tags")
@RequiredArgsConstructor
public class TagController {
	
	private final TagService tagService;
	private final UserTagService userTagService;
	
	@GetMapping
	@ApiOperation(summary = "List tags with a query.")
	public Page<Tag> list(
		Pageable pageable,
		@Query String query
	) {
		return tagService.search(query, pageable);
	}
	
	@PostMapping
	@ApiOperation(summary = "Create a tag.")
	public Tag create(
		@Body @Valid TagCreateForm form
	) {
		return tagService.create(form.getName(), form.getColor());
	}
	
	@GetMapping(path = "{id}")
	@ApiOperation(summary = "Show a tag.")
	public Tag show(
		@Variable long id
	) {
		return tagService.find(id)
			.orElseThrow(() -> new TagNotFoundException(id));
	}
	
	@GetMapping(path = "{id}/users")
	@ApiOperation(summary = "Show a tag.")
	public Page<User> listUsers(
		Pageable pageable,
		@Variable long id
	) {
		final var tag = show(id);
		
		return userTagService.findAll(tag, pageable);
	}
	
}