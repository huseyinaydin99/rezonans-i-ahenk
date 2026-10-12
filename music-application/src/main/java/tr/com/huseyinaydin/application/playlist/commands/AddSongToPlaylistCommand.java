package tr.com.huseyinaydin.application.playlist.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

import java.util.UUID;

public record AddSongToPlaylistCommand(String playlistId, String songId) implements ICommand<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(AddSongToPlaylistCommand.class);

    public AddSongToPlaylistCommand(UUID playlistId, UUID songId) {
        this(playlistId != null ? playlistId.toString() : null, songId != null ? songId.toString() : null);
    }

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
                    boolean success = playlistService.addSongToPlaylist(command.playlistId(), command.songId());
                    log.info("Song {} added to Playlist {}: {}", command.songId(), command.playlistId(), success);
                    return success;
                } catch (Exception e) {
                    log.error("Error adding song {} to playlist {}: {}", command.songId(), command.playlistId(), e.getMessage());
                    return false;
                }
            }
            return true;
        }
    }
}
