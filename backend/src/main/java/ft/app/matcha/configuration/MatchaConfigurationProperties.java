package ft.app.matcha.configuration;

import ft.framework.property.annotation.ConfigurationProperties;
import ft.framework.validation.annotation.Valid;
import lombok.Data;

@Valid
@Data
@ConfigurationProperties(prefix = "matcha")
public class MatchaConfigurationProperties {
	
	private long maximumPictureCount = 5;
	private String pictureStorage = "./pictures/";

	private long maximumTagCount = 5;
	
}