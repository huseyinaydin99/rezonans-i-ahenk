package tr.com.huseyinaydin.application.playlist.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record RemoveSongFromPlaylistCommand(UUID playlistId, UUID songId) implements ICommand<Boolean> {
    @Component
    public static class Handler implements ICommandHandler<RemoveSongFromPlaylistCommand, Boolean> {
        @Override
        public Boolean handle(RemoveSongFromPlaylistCommand command) {
            // Logic: Find PlaylistSong entity and delete from repository
            System.out.println("Song " + command.songId() + " removed from Playlist " + command.playlistId());
            return true;
        }
    }
}
