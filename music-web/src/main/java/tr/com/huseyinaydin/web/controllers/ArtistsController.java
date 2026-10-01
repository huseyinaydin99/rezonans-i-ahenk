package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.artist.commands.CreateArtistCommand;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistsController extends BaseController {
    public ArtistsController(Mediator mediator) { super(mediator); }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody CreateArtistCommand command) {
        return ResponseEntity.ok(mediator.send(command));
    }
}
