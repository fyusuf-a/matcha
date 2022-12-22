package ft.app.matcha.domain.picture;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import com.google.common.io.Files;

import ft.app.matcha.configuration.MatchaConfigurationProperties;
import ft.app.matcha.domain.picture.exception.MaximumPictureCountException;
import ft.app.matcha.domain.user.User;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import lombok.SneakyThrows;

public class PictureService {
	
	private final PictureRepository repository;
	private final long maxPictureCount;
	private final String storage;
	
	public PictureService(PictureRepository repository, MatchaConfigurationProperties matchaConfigurationProperties) {
		this.repository = repository;
		this.maxPictureCount = matchaConfigurationProperties.getMaximumPictureCount();
		this.storage = matchaConfigurationProperties.getPictureStorage();
	}

	public Page<Picture> findAll(User user, Pageable pageable) {
		return repository.findAllByUser(user, pageable);
	}
	
	public Picture upload(User user, byte[] bytes) {
		if (hasReachedMaximum(user)) {
			throw new MaximumPictureCountException(maxPictureCount);
		}
		
		final var path = store(bytes);
		
		return repository.save(
			new Picture()
				.setUser(user)
				.setPath(path)
				.setCreatedAt(LocalDateTime.now())
		);
	}
	
	@SneakyThrows
	public String store(byte[] bytes) {
		final var path = Paths.get(storage, UUID.randomUUID().toString());
		
		Files.write(bytes, path.toFile());
		
		return path.toString();
	}
	
	public boolean hasReachedMaximum(User user) {
		final var count = repository.countByUser(user);
		
		return count >= maxPictureCount;
	}
	
}