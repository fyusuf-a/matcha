package ft.app.matcha.domain.picture;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.Part;

import ft.framework.mvc.annotation.Controller;
import ft.framework.mvc.annotation.FormData;
import ft.framework.mvc.annotation.PostMapping;
import ft.framework.mvc.annotation.RequestMapping;
import ft.framework.util.MediaTypes;

@Controller
@RequestMapping(path = "/pictures")
public class PictureController {
	
//	@Authenticated
	@PostMapping(produce = MediaTypes.PNG, consume = MediaTypes.FORM_DATA)
	public InputStream index(
		@FormData Part file
	) throws IOException, ServletException {
		return file.getInputStream();
	}
	
}