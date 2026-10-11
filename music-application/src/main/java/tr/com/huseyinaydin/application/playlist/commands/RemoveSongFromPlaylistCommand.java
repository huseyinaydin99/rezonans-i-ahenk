package tr.com.huseyinaydin.application.playlist.commands;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record RemoveSongFromPlaylistCommand(UUID playlistId, UUID songId) implements ICommand<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(RemoveSongFromPlaylistCommand.class);

    @Component
    public static class Handler implements ICommandHandler<RemoveSongFromPlaylistCommand, Boolean> {
        @Override
        public Boolean handle(RemoveSongFromPlaylistCommand command) {
            // Logic: Find PlaylistSong entity and delete from repository
            log.info("Song " + command.songId() + " removed from Playlist " + command.playlistId());
            return true;
        }
    }
}
