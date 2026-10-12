package tr.com.huseyinaydin.application.playlist.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.playlist.dtos.CreatePlaylistDto;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.domain.playlist.Playlist;
import tr.com.huseyinaydin.domain.repositories.IPlaylistRepository;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record CreatePlaylistCommand(String name, UUID userId) implements ICommand<UUID> {
    @Component
    public static class Handler implements ICommandHandler<CreatePlaylistCommand, UUID> {
        private final IPlaylistRepository repository;
        private final IPlaylistService playlistService;

        public Handler(IPlaylistRepository repository,
                       @Autowired(required = false) IPlaylistService playlistService) {
            this.repository = repository;
            this.playlistService = playlistService;
        }

        @Override
        public UUID handle(CreatePlaylistCommand command) {
            Playlist playlist = new Playlist(command.name(), command.userId());
            if (repository != null) {
                try {
                    repository.save(playlist);
                } catch (Exception ignored) {}
            }
            if (playlistService != null) {
                try {
                    CreatePlaylistDto dto = new CreatePlaylistDto();
                    dto.setUserId(command.userId() != null ? command.userId().toString() : "");
                    dto.setName(command.name());
                    playlistService.createPlaylist(dto);
                } catch (Exception ignored) {}
            }
            return playlist.getId();
        }
    }
}
