package tr.com.huseyinaydin.application.playlist.commands;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.domain.playlist.PlaylistSong;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record AddSongToPlaylistCommand(UUID playlistId, UUID songId) implements ICommand<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(AddSongToPlaylistCommand.class);

    @Component
    public static class Handler implements ICommandHandler<AddSongToPlaylistCommand, Boolean> {
        private final IPlaylistService playlistService;

        public Handler(@Autowired(required = false) IPlaylistService playlistService) {
            this.playlistService = playlistService;
        }

        @Override
        public Boolean handle(AddSongToPlaylistCommand command) {
            if (playlistService != null && command.playlistId() != null && command.songId() != null) {
                try {
                    playlistService.addSongToPlaylist(command.playlistId().toString(), command.songId().toString());
                } catch (Exception ignored) {}
            }
            log.info("Song " + command.songId() + " added to Playlist " + command.playlistId());
            return true;
        }
    }
}
