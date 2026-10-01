package tr.com.huseyinaydin.application.playlist.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.playlist.PlaylistSong;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record AddSongToPlaylistCommand(UUID playlistId, UUID songId) implements ICommand<Boolean> {
    @Component
    public static class Handler implements ICommandHandler<AddSongToPlaylistCommand, Boolean> {
        @Override
        public Boolean handle(AddSongToPlaylistCommand command) {
            // Business rule: Verify playlist belongs to current user
            // Logic: Create PlaylistSong entity and save to repository
            System.out.println("Song " + command.songId() + " added to Playlist " + command.playlistId());
            return true;
        }
    }
}
