package ft.app.matcha.domain.auth;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

import ft.app.matcha.domain.auth.exception.JwtException;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.domain.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

public class JwtService {
	
	private final UserRepository userRepository;
	private final Key key;
	private final Duration expiration;
	
	public JwtService(UserRepository userRepository, AuthConfiguration configuration) {
		this.userRepository = userRepository;
		this.key = Keys.hmacShaKeyFor(configuration.getJwtSecret().getBytes());
		this.expiration = configuration.getJwtExpiration();
	}
	
	public String generate(User user) {
		final var now = new Date();
		
		return Jwts.builder()
			.setHeaderParam("typ", "JWT")
			.setSubject(String.valueOf(user.getId()))
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + expiration.toMillis()))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}
	
	public User decode(String jwtString) {
		try {
			final var jwt = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parse(jwtString);
			
			final var claims = (Claims) jwt.getBody();
			if (isExpired(claims)) {
				throw JwtException.expired();
			}
			
			final var subject = Long.parseLong(claims.getSubject());
			
			return userRepository.getById(subject);
		} catch (SignatureException exception) {
			throw JwtException.badSignature();
		} catch (MalformedJwtException exception) {
			throw JwtException.malformed();
		}
	}
	
	public boolean isExpired(Claims claims) {
		return claims.getExpiration().before(new Date());
	}
	
}