package tr.com.huseyinaydin.application.playlist.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record SharePlaylistCommand(UUID playlistId) implements ICommand<String> {
    @Component
    public static class Handler implements ICommandHandler<SharePlaylistCommand, String> {
        @Override
        public String handle(SharePlaylistCommand command) {
            // Generate a unique, secure sharing token/hash for the playlist
            String shareHash = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            return "https://musicplatform.com/share/pl/" + shareHash;
        }
    }
}
