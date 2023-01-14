package ft.app.matcha.domain.notification;

import ft.app.matcha.domain.notification.exception.InvalidNotificationOwnerException;
import ft.app.matcha.domain.notification.exception.NotificationNotFoundException;
import ft.app.matcha.domain.notification.model.NotificationPatchForm;
import ft.app.matcha.domain.user.User;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Body;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.PatchMapping;
import ft.framework.mvc.annotation.Principal;
import ft.framework.mvc.annotation.Query;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.annotation.Variable;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.swagger.annotation.ApiOperation;
import ft.framework.validation.annotation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = "/notifications")
@RequiredArgsConstructor
public class NotificationController {
	
	private final NotificationService notificationService;
	
	@Authenticated
	@GetMapping
	@ApiOperation(summary = "List received notifications.")
	public Page<Notification> list(
		Pageable pageable,
		@Query(required = false) boolean includeAll,
		@Principal User currentUser
	) {
		return notificationService.findAll(currentUser, includeAll, pageable);
	}
	
	@Authenticated
	@GetMapping(path = "{id}")
	@ApiOperation(summary = "Show a notification.")
	public Notification show(
		@Variable long id,
		@Principal User currentUser
	) {
		return getNotification(id, currentUser);
	}
	
	@Authenticated
	@PatchMapping(path = "{id}")
	@ApiOperation(summary = "Update a notification.")
	public Notification patch(
		@Variable long id,
		@Body @Valid NotificationPatchForm form,
		@Principal User currentUser
	) {
		final var notification = getNotification(id, currentUser);
		
		return notificationService.patch(notification, form);
	}
	
	public Notification getNotification(long id, User currentUser) {
		final var notification = notificationService.find(id)
			.orElseThrow(() -> new NotificationNotFoundException(id));
		
		if (notification.getUser().getId() != currentUser.getId()) {
			throw new InvalidNotificationOwnerException(id);
		}
		
		return notification;
	}
	
}