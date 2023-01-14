package ft.app.matcha.web;

import java.io.InputStream;

import javax.servlet.http.Part;

import org.eclipse.jetty.http.HttpStatus;

import ft.app.matcha.domain.picture.Picture;
import ft.app.matcha.domain.picture.PictureService;
import ft.app.matcha.domain.picture.exception.InvalidPictureOwnerException;
import ft.app.matcha.domain.picture.exception.PictureNotFoundException;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserService;
import ft.app.matcha.domain.user.exception.UserNotFoundException;
import ft.app.matcha.web.dto.PictureDto;
import ft.app.matcha.web.form.PicturePatchForm;
import ft.app.matcha.web.map.PictureMapper;
import ft.framework.mvc.annotation.Authenticated;
import ft.framework.mvc.annotation.Body;
import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.DeleteMapping;
import ft.framework.mvc.annotation.FormData;
import ft.framework.mvc.annotation.GetMapping;
import ft.framework.mvc.annotation.PatchMapping;
import ft.framework.mvc.annotation.PostMapping;
import ft.framework.mvc.annotation.Principal;
import ft.framework.mvc.annotation.Query;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.mvc.annotation.ResponseStatus;
import ft.framework.mvc.annotation.Variable;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import ft.framework.swagger.annotation.ApiOperation;
import ft.framework.util.MediaTypes;
import ft.framework.validation.annotation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Controller
@RequestMapping(path = "/pictures")
@RequiredArgsConstructor
public class PictureController {
	
	private final UserService userService;
	private final PictureService pictureService;
	private final PictureMapper pictureMapper;
	
	@GetMapping
	@ApiOperation(summary = "List the picture of an user.")
	public Page<PictureDto> list(
		Pageable pageable,
		@Query(required = false) Long userId,
		@Principal User currentUser
	) {
		final var page = doList(pageable, userId, currentUser);
		return pictureMapper.toDto(page);
	}
	
	private Page<Picture> doList(
		Pageable pageable,
		Long userId,
		User currentUser
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
	@ApiOperation(summary = "Upload a new picture.")
	public PictureDto upload(
		@FormData Part file,
		@Principal User user
	) {
		try (final var inputStream = file.getInputStream()) {
			final var bytes = inputStream.readAllBytes();
			
			final var picture = pictureService.upload(user, bytes);
			return pictureMapper.toDto(picture);
		}
	}
	
	@GetMapping(path = "{id}")
	@ApiOperation(summary = "Show a picture.")
	public PictureDto show(
		@Variable long id
	) {
		final var picture = getPicture(id);
		
		return pictureMapper.toDto(picture);
	}
	
	@PatchMapping(path = "{id}")
	@ApiOperation(summary = "Update a picture.")
	public PictureDto patch(
		@Variable long id,
		@Body @Valid PicturePatchForm form
	) {
		final var picture = getPicture(id);
		
		if (Boolean.TRUE.equals(form.getIsDefault())) {
			pictureService.setDefault(picture);
		}
		
		return pictureMapper.toDto(picture);
	}
	
	@GetMapping(path = "{id}/view", produce = MediaTypes.PNG)
	@ApiOperation(summary = "View a picture.")
	public InputStream view(
		@Variable long id
	) {
		final var picture = getPicture(id);
		
		return pictureService.read(picture);
	}
	
	@Authenticated
	@ResponseStatus(HttpStatus.ACCEPTED_202)
	@DeleteMapping(path = "{id}")
	@ApiOperation(summary = "Delete a picture.")
	public void delete(
		@Variable long id,
		@Principal User user
	) {
		final var picture = getPicture(id);
		
		if (picture.getUser().getId() != user.getId()) {
			throw new InvalidPictureOwnerException(id);
		}
		
		pictureService.delete(picture);
	}
	
	public Picture getPicture(long id) {
		return pictureService.find(id)
			.orElseThrow(() -> new PictureNotFoundException(id));
	}
	
}