package ft.app.matcha.domain.report.event;

import ft.app.matcha.domain.report.Report;
import ft.framework.event.ApplicationEvent;
import lombok.Getter;

@Getter
@SuppressWarnings("serial")
public class ReportCreatedEvent extends ApplicationEvent {
	
	private final Report report;
	
	public ReportCreatedEvent(Object source, Report report) {
		super(source);
		
		this.report = report;
	}
	
}