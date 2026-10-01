package tr.com.huseyinaydin.application.playlist.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.playlist.Playlist;
import tr.com.huseyinaydin.domain.repositories.IPlaylistRepository;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record CreatePlaylistCommand(String name, UUID userId) implements ICommand<UUID> {
    @Component
    public static class Handler implements ICommandHandler<CreatePlaylistCommand, UUID> {
        private final IPlaylistRepository repository;
        public Handler(IPlaylistRepository repository) { this.repository = repository; }
        @Override
        public UUID handle(CreatePlaylistCommand command) {
            Playlist playlist = new Playlist(command.name(), command.userId());
            repository.save(playlist);
            return playlist.getId();
        }
    }
}
