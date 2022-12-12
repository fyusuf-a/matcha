package ft.app.matcha.configuration;

import java.time.Duration;

import org.apache.commons.lang3.RandomStringUtils;

import ft.framework.property.annotation.ConfigurationProperties;
import ft.framework.validation.annotation.Valid;
import lombok.Data;

@Valid
@Data
@ConfigurationProperties(prefix = "auth")
public class AuthConfigurationProperties {
	
	private String jwtSecret = RandomStringUtils.randomAlphanumeric(128);
	private Duration jwtExpiration = Duration.ofMinutes(15);
	
	private int refreshTokenLength = 128;
	private Duration refreshTokenExpiration = Duration.ofDays(7);

	private int emailTokenLength = 128;
	private Duration emailTokenExpiration = Duration.ofDays(1);
	
}