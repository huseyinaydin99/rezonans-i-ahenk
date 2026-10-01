package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.playlist.commands.CreatePlaylistCommand;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistsController extends BaseController {
    public PlaylistsController(Mediator mediator) { super(mediator); }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody CreatePlaylistCommand command) {
        return ResponseEntity.ok(mediator.send(command));
    }
}
