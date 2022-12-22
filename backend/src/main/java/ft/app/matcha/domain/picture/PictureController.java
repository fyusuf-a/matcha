package ft.app.matcha.domain.picture;

import java.io.InputStream;

import javax.servlet.http.Part;

import org.eclipse.jetty.http.HttpStatus;

import ft.app.matcha.domain.picture.exception.InvalidPictureOwnerException;
import ft.app.matcha.domain.picture.exception.PictureNotFoundException;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserService;
import ft.app.matcha.domain.user.exception.UserNotFoundException;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.DeleteMapping;
import ft.framework.mvc.annotation.FormData;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.PostMapping;
import ft.framework.mvc.annotation.Principal;
import ft.framework.mvc.annotation.Query;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.annotation.ResponseStatus;
import ft.framework.mvc.annotation.Variable;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.util.MediaTypes;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Controller
@RequestMapping(path = "/pictures")
@RequiredArgsConstructor
public class PictureController {
	
	private final UserService userService;
	private final PictureService pictureService;
	
	@GetMapping
	public Page<Picture> list(
		Pageable pageable,
		@Query(required = false) Long userId,
		@Principal User currentUser
	) {
		if (userId != null) {
			final var user = userService.find(userId).orElseThrow(() -> new UserNotFoundException(userId));
			return pictureService.findAll(user, pageable);
		}
		
		if (currentUser != null) {
			return pictureService.findAll(currentUser, pageable);
		}
		
		return Page.empty(pageable);
	}
	
	@SneakyThrows
	@Authenticated
	@PostMapping(consume = MediaTypes.FORM_DATA)
	public Picture upload(
		@FormData Part file,
		@Principal User user
	) {
		try (final var inputStream = file.getInputStream()) {
			final var bytes = inputStream.readAllBytes();
			
			return pictureService.upload(user, bytes);
		}
	}
	
	@GetMapping(path = "{id}")
	public Picture show(
		@Variable long id
	) {
		return pictureService.findById(id)
			.orElseThrow(() -> new PictureNotFoundException(id));
	}
	
	@GetMapping(path = "{id}/view", produce = MediaTypes.PNG)
	public InputStream view(
		@Variable long id
	) {
		final var picture = show(id);
		
		return pictureService.read(picture);
	}
	
	@Authenticated
	@ResponseStatus(HttpStatus.ACCEPTED_202)
	@DeleteMapping(path = "{id}")
	public void delete(
		@Variable long id,
		@Principal User user
	) {
		final var picture = show(id);
		
		if (picture.getUser().getId() != user.getId()) {
			throw new InvalidPictureOwnerException(id);
		}
		
		pictureService.delete(picture);
	}
	
}