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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tr.com.huseyinaydin.application.song.commands.PlaySongCommand;
import tr.com.huseyinaydin.application.song.dtos.PlaySongResult;

import java.util.HashMap;

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

    @PostMapping("/api/songs/{id}/play")
    public ResponseEntity<Map<String, Object>> playSong(@PathVariable("id") Long id) {
        return executePlaySong(id);
    }

    @RequestMapping(
        value = {"/Song/Play/{id}", "/api/Song/Play/{id}"},
        method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ResponseEntity<Map<String, Object>> playSongLegacy(@PathVariable("id") Long id) {
        return executePlaySong(id);
    }

    private ResponseEntity<Map<String, Object>> executePlaySong(Long id) {
        var user = CurrentUser.get().orElse(null);
        if (user == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Müzik dinlemek için giriş yapmalısınız.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        String tier = user.tier();
        PlaySongResult result = mediator.send(new PlaySongCommand(id, user.email(), tier));

        if (!result.authorized()) {
            Map<String, Object> forbidden = new HashMap<>();
            forbidden.put("requiredTier", result.requiredTier());
            forbidden.put("message", result.message());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(forbidden);
        }

        if (metrics != null) {
            metrics.incrementSongsPlayed(tier);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("songId", result.songId());
        response.put("title", result.title());
        response.put("artist", result.artist());
        response.put("audioUrl", result.audioUrl());
        response.put("cover", result.cover());
        response.put("playCount", result.playCount());
        response.put("playbackUrl", result.audioUrl());

        return ResponseEntity.ok(response);
    }
}
