package ft.app.matcha.domain.report;

import ft.app.matcha.domain.report.model.ReportCreateForm;
import ft.app.matcha.domain.report.model.ReportDto;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserService;
import ft.app.matcha.domain.user.exception.UserNotFoundException;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Body;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.PostMapping;
import ft.framework.mvc.annotation.Principal;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.swagger.annotation.ApiOperation;
import ft.framework.validation.annotation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(path = "/reports")
@RequiredArgsConstructor
public class ReportController {
	
	private final ReportService reportService;
	private final UserService userService;
	
	@Authenticated
	@GetMapping
	@ApiOperation(summary = "Show the report on your account.")
	public Page<ReportDto> list(
		Pageable pageable,
		@Principal User currentUser
	) {
		return reportService.findAll(currentUser, pageable)
			.map(ReportDto::from);
	}
	
	@Authenticated
	@PostMapping
	@ApiOperation(summary = "Report someone.")
	public Report create(
		@Body @Valid ReportCreateForm form,
		@Principal User currentUser
	) {
		final var userId = form.getUserId();
		final var user = userService.find(userId).orElseThrow(() -> new UserNotFoundException(userId));
		
		return reportService.create(form.getReason(), user, currentUser);
	}
	
}