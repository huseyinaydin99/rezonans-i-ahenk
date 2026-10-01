package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.application.song.queries.GetRecommendedSongsQuery;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationsController extends BaseController {
    public RecommendationsController(Mediator mediator) { super(mediator); }

    @GetMapping("/{userId}")
    public ResponseEntity<List<SongResponse>> getRecommendations(@PathVariable UUID userId, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(mediator.send(new GetRecommendedSongsQuery(userId, limit)));
    }
}
