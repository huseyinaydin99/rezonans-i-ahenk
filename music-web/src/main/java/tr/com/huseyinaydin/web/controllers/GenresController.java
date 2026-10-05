package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.genre.commands.CreateGenreCommand;
import tr.com.huseyinaydin.application.genre.queries.GetSongsByGenreQuery;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/genres")
public class GenresController extends BaseController {

    public GenresController(Mediator mediator) { super(mediator); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UUID> create(@RequestBody CreateGenreCommand command) {
        return ResponseEntity.ok(mediator.send(command));
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<List<SongResponse>> getSongsByGenre(@PathVariable UUID id) {
        return ResponseEntity.ok(mediator.query(new GetSongsByGenreQuery(id)));
    }
}
