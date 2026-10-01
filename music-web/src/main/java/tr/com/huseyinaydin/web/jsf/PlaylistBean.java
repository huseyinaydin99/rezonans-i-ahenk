package tr.com.huseyinaydin.web.jsf;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.playlist.commands.AddSongToPlaylistCommand;
import tr.com.huseyinaydin.application.playlist.commands.RemoveSongFromPlaylistCommand;
import tr.com.huseyinaydin.application.playlist.commands.SharePlaylistCommand;

import java.util.UUID;

@Named
@RequestScoped
public class PlaylistBean {
    @Autowired
    private Mediator mediator;

    private String generatedShareLink;

    public void addSong(UUID playlistId, UUID songId) {
        mediator.send(new AddSongToPlaylistCommand(playlistId, songId));
    }

    public void removeSong(UUID playlistId, UUID songId) {
        mediator.send(new RemoveSongFromPlaylistCommand(playlistId, songId));
    }

    public void generateShareLink(UUID playlistId) {
        this.generatedShareLink = mediator.send(new SharePlaylistCommand(playlistId));
    }

    public String getGeneratedShareLink() {
        return generatedShareLink;
    }
}
