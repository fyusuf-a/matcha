package ft.app.matcha.domain.report.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import ft.app.matcha.domain.report.Report;
import ft.app.matcha.domain.user.User;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReportDto {
	
	private long id;
	private String reason;
	private User reporter;
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime createdAt;
	
	public static ReportDto from(Report report) {
		return new ReportDto()
			.setId(report.getId())
			.setReason(report.getReason())
			.setReporter(report.getReporter())
			.setCreatedAt(report.getCreatedAt());
	}
	
}