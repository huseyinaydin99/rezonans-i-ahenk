package tr.com.huseyinaydin.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.List;

import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.song.queries.SearchSongsQuery;
import tr.com.huseyinaydin.application.song.queries.GetSongPlaybackInfoQuery;
import tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics;
import tr.com.huseyinaydin.sharedkernel.exception.AuthorizationException;
import tr.com.huseyinaydin.web.security.CurrentUser;

@RestController
public class SongController extends BaseController {

    private final MusicPlatformMetrics metrics;

    @Autowired
    public SongController(Mediator mediator,
                          @Autowired(required = false) MusicPlatformMetrics metrics) {
        super(mediator);
        this.metrics = metrics;
    }

    @GetMapping(value = {"/Song/Search", "/api/Song/Search"})
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam(value="q", required=false) String q) {
        List<Map<String, Object>> results = mediator.query(new SearchSongsQuery(q));
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = {"/Song/Play/{id}", "/api/Song/Play/{id}"})
    public ResponseEntity<Map<String, Object>> playSong(@PathVariable("id") Long id) {
        var user = CurrentUser.get().orElse(null);
        if (user == null) {
            throw new AuthorizationException("playSong", "Müzik dinlemek için giriş yapmalısınız.");
        }

        String tier = user.tier();
        Map<String, Object> response = mediator.query(new GetSongPlaybackInfoQuery(id, user.email(), tier));

        if (metrics != null) {
            metrics.incrementSongsPlayed(tier);
        }

        return ResponseEntity.ok(response);
    }
}
