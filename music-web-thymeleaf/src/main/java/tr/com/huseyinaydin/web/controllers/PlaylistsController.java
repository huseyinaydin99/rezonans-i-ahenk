package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.playlist.commands.CreatePlaylistCommand;
import tr.com.huseyinaydin.application.playlist.commands.AddSongToPlaylistCommand;
import tr.com.huseyinaydin.application.playlist.commands.RemoveSongFromPlaylistCommand;
import tr.com.huseyinaydin.application.playlist.commands.SharePlaylistCommand;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistsController extends BaseController {
    public PlaylistsController(Mediator mediator) { super(mediator); }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody CreatePlaylistCommand command) {
        return ResponseEntity.ok(mediator.send(command));
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Boolean> addSong(@PathVariable UUID playlistId, @PathVariable UUID songId) {
        return ResponseEntity.ok(mediator.send(new AddSongToPlaylistCommand(playlistId, songId)));
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Boolean> removeSong(@PathVariable UUID playlistId, @PathVariable UUID songId) {
        return ResponseEntity.ok(mediator.send(new RemoveSongFromPlaylistCommand(playlistId, songId)));
    }

    @PostMapping("/{playlistId}/share")
    public ResponseEntity<String> share(@PathVariable UUID playlistId) {
        return ResponseEntity.ok(mediator.send(new SharePlaylistCommand(playlistId)));
    }
}
