package ft.app.matcha.domain.heartbeat;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import ft.app.matcha.domain.user.User;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

@Entity
@Table
@Data
@Accessors(chain = true)
@FieldNameConstants
public class Heartbeat {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne(optional = false)
	@JsonIgnore
	private User user;
	
	@Column(nullable = false)
	private String ip;
	
	@Column
	private Double latitude;
	
	@Column
	private Double longitude;
	
	@Column
	private String city;
	
	@Column
	private String country;
	
	@Column(nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime createdAt;
	
	public long getUserId() {
		return user.getId();
	}
	
}