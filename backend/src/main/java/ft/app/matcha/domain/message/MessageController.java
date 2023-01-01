package ft.app.matcha.domain.message;

import ft.app.matcha.domain.message.model.MessageCreateForm;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserService;
import ft.app.matcha.domain.user.exception.UserNotFoundException;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Body;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.PostMapping;
import ft.framework.mvc.annotation.Principal;
import ft.framework.mvc.annotation.Query;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.validation.annotation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = "/messages")
@RequiredArgsConstructor
public class MessageController {
	
	private final MessageService messageService;
	private final UserService userService;
	
	@Authenticated
	@GetMapping
	public Page<Message> list(
		Pageable pageable,
		@Query long peerId,
		@Principal User currentUser
	) {
		final var peer = userService.find(peerId)
			.orElseThrow(() -> new UserNotFoundException(peerId));
		
		return messageService.findAll(currentUser, peer, pageable);
	}
	
	@Authenticated
	@PostMapping
	public Message create(
		@Body @Valid MessageCreateForm form,
		@Principal User currentUser
	) {
		final var peerId = form.getPeerId();
		final var peer = userService.find(peerId)
			.orElseThrow(() -> new UserNotFoundException(peerId));
		
		return messageService.create(currentUser, peer, form.getContent());
	}
	
}