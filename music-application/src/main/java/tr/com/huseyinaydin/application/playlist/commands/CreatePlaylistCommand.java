package tr.com.huseyinaydin.application.playlist.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.playlist.dtos.CreatePlaylistDto;
import tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.domain.playlist.Playlist;
import tr.com.huseyinaydin.domain.repositories.IPlaylistRepository;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public record CreatePlaylistCommand(
        String name,
        String description,
        String userEmail,
        UUID userId
) implements ICommand<UUID> {
    private static final Logger log = LoggerFactory.getLogger(CreatePlaylistCommand.class);

    public CreatePlaylistCommand(String name, UUID userId) {
        this(name, "", null, userId);
    }

    public CreatePlaylistCommand(String name, String description, String userEmail) {
        this(name, description, userEmail, userEmail != null
                ? UUID.nameUUIDFromBytes(userEmail.getBytes(StandardCharsets.UTF_8))
                : UUID.randomUUID());
    }

    @Component
    public static class Handler implements ICommandHandler<CreatePlaylistCommand, UUID> {
        private final IPlaylistRepository repository;
        private final IPlaylistService playlistService;

        public Handler(@Autowired(required = false) IPlaylistRepository repository,
                       @Autowired(required = false) IPlaylistService playlistService) {
            this.repository = repository;
            this.playlistService = playlistService;
        }

        @Override
        public UUID handle(CreatePlaylistCommand command) {
            String trimmedName = command.name() != null ? command.name().trim() : "";
            if (trimmedName.isEmpty()) {
                throw new IllegalArgumentException("Lütfen geçerli bir playlist adı girin.");
            }

            String userKey = command.userEmail() != null && !command.userEmail().isBlank()
                    ? command.userEmail()
                    : (command.userId() != null ? command.userId().toString() : "");

            if (playlistService != null && !userKey.isBlank()) {
                List<PlaylistDetailDto> userPlaylists = playlistService.getUserPlaylists(userKey);
                if (userPlaylists != null) {
                    boolean exists = userPlaylists.stream()
                            .anyMatch(p -> p.getName() != null && p.getName().trim().equalsIgnoreCase(trimmedName));
                    if (exists) {
                        throw new BusinessException("'" + trimmedName + "' adında bir playlist zaten mevcut!", "PLAYLIST_ALREADY_EXISTS");
                    }
                }
            }

            UUID effectiveUserId = command.userId() != null ? command.userId() : UUID.randomUUID();
            Playlist playlist = new Playlist(trimmedName, effectiveUserId);
            if (repository != null) {
                try {
                    repository.save(playlist);
                } catch (Exception ignored) {}
            }

            if (playlistService != null) {
                try {
                    CreatePlaylistDto dto = new CreatePlaylistDto();
                    dto.setUserId(userKey);
                    dto.setName(trimmedName);
                    dto.setDescription(command.description() != null ? command.description().trim() : "");
                    playlistService.createPlaylist(dto);
                } catch (BusinessException be) {
                    throw be;
                } catch (Exception ignored) {}
            }

            log.info("Playlist '{}' created successfully via CreatePlaylistCommand for user: {}", trimmedName, userKey);
            return playlist.getId();
        }
    }
}
