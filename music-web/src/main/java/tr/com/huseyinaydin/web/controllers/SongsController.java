package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.song.commands.CreateSongCommand;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.application.song.queries.GetByIdSongQuery;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/songs")
public class SongsController extends BaseController {

    public SongsController(Mediator mediator) {
        super(mediator);
    }

    @PostMapping
    public ResponseEntity<SongResponse> create(@Valid @RequestBody CreateSongCommand command) {
        SongResponse response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getById(@PathVariable UUID id) {
        SongResponse response = mediator.send(new GetByIdSongQuery(id));
        return ResponseEntity.ok(response);
    }
}
