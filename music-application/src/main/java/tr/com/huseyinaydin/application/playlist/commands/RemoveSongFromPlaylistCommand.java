package tr.com.huseyinaydin.application.playlist.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

import java.util.UUID;

public record RemoveSongFromPlaylistCommand(String playlistId, String songId) implements ICommand<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(RemoveSongFromPlaylistCommand.class);

    public RemoveSongFromPlaylistCommand(UUID playlistId, UUID songId) {
        this(playlistId != null ? playlistId.toString() : null, songId != null ? songId.toString() : null);
    }

    @Component
    public static class Handler implements ICommandHandler<RemoveSongFromPlaylistCommand, Boolean> {
        private final IPlaylistService playlistService;

        public Handler(@Autowired(required = false) IPlaylistService playlistService) {
            this.playlistService = playlistService;
        }

        @Override
        public Boolean handle(RemoveSongFromPlaylistCommand command) {
            log.info("Removing song {} from playlist {}", command.songId(), command.playlistId());
            if (playlistService != null && command.playlistId() != null && command.songId() != null) {
                playlistService.removeSongFromPlaylist(command.playlistId(), command.songId());
            }
            return true;
        }
    }
}
