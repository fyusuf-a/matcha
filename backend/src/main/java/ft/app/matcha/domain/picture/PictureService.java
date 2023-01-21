package ft.app.matcha.domain.picture;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import ft.app.matcha.configuration.MatchaConfigurationProperties;
import ft.app.matcha.domain.picture.exception.MaximumPictureCountException;
import ft.app.matcha.domain.user.User;
import ft.framework.mvc.domain.Page;
import ft.framework.mvc.domain.Pageable;
import lombok.SneakyThrows;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PictureService {
	
	private final PictureRepository repository;
	private final DefaultPictureRepository defaultRepository;
	private final OkHttpClient httpClient;
	private final long maxPictureCount;
	private final String storage;
	
	public PictureService(PictureRepository repository, DefaultPictureRepository defaultRepository, OkHttpClient httpClient, MatchaConfigurationProperties matchaConfigurationProperties) {
		this.repository = repository;
		this.defaultRepository = defaultRepository;
		this.httpClient = httpClient;
		this.maxPictureCount = matchaConfigurationProperties.getMaximumPictureCount();
		this.storage = matchaConfigurationProperties.getPictureStorage();
	}
	
	public Page<Picture> findAll(User user, Pageable pageable) {
		return repository.findAllByUser(user, pageable);
	}
	
	public Optional<Picture> find(long id) {
		return repository.findById(id);
	}
	
	public Optional<DefaultPicture> getDefault(User user) {
		return defaultRepository.findByUser(user);
	}
	
	public DefaultPicture setDefault(Picture picture) {
		final var user = picture.getUser();
		
		final var default_ = defaultRepository
			.findByUser(user)
			.orElseGet(() -> new DefaultPicture().setUser(user))
			.setPicture(picture)
			.setSelectedAt(LocalDateTime.now());
		
		return defaultRepository.save(default_);
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
	public Picture upload(User user, String url) {
		final var call = httpClient.newCall(new Request.Builder()
			.get()
			.url(HttpUrl.parse(url))
			.build());
		
		try (final var response = call.execute()) {
			try (final var body = response.body()) {
				final var bytes = body.bytes();
				
				return upload(user, bytes);
			}
		}
	}
	
	@SneakyThrows
	public String store(byte[] bytes) {
		final var path = UUID.randomUUID().toString();
		final var dataPath = Paths.get(storage, path);
		
		Files.write(dataPath, bytes);
		
		return path;
	}
	
	public boolean hasReachedMaximum(User user) {
		final var count = repository.countByUser(user);
		
		return count >= maxPictureCount;
	}
	
	@SneakyThrows
	public void delete(Picture picture) {
		Files.deleteIfExists(toPath(picture));
		
		repository.delete(picture);
	}
	
	@SneakyThrows
	public InputStream read(Picture picture) {
		return new FileInputStream(toPath(picture).toFile());
	}
	
	public Path toPath(Picture picture) {
		return Paths.get(storage, picture.getPath());
	}
	
}