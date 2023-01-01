package ft.app.matcha.domain.notification.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationPatchForm {
	
	private Boolean read;
	
}