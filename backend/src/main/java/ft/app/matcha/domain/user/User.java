package ft.app.matcha.domain.user;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

@Entity
@Table
@Data
@Accessors(chain = true)
@FieldNameConstants
public class User {
	
	public static final int LOGIN_MIN_LENGTH = 3;
	public static final int LOGIN_MAX_LENGTH = 48;
	public static final int EMAIL_MAX_LENGTH = 128;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false, unique = true)
	private String login;
	
	@Column(nullable = false)
	@JsonIgnore
	private String password;
	
	@Column(nullable = false)
	@JsonIgnore
	private String email;
	
	@Column(nullable = false)
	private String firstName;
	
	@Column(nullable = false)
	private String lastName;
	
	@Column
	private String bio;
	
	@Column(nullable = false)
	private boolean emailConfirmed;
	
	@Column
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime emailConfirmedAt;
	
}