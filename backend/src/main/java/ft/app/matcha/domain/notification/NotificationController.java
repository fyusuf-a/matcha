package ft.app.matcha.domain.notification;

import ft.app.matcha.domain.user.User;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.Principal;
import ft.framework.mvc.annotation.Query;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = "/notifications")
@RequiredArgsConstructor
public class NotificationController {
	
	private final NotificationService notificationService;
	
	@Authenticated
	@GetMapping
	public Page<Notification> list(
		Pageable pageable,
		@Query(required = false) boolean includeAll,
		@Principal User currentUser
	) {
		return notificationService.findAll(currentUser, includeAll, pageable);
	}
	
}