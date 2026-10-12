package tr.com.huseyinaydin.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.application.ports.IPlaybackService;

@RestController
public class SongController {

    private final ISongSearchService songSearchService;
    private final IPlaybackService playbackService;
    private final tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics metrics;

    @Autowired
    public SongController(ISongSearchService songSearchService,
                          IPlaybackService playbackService,
                          @Autowired(required = false) tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics metrics) {
        this.songSearchService = songSearchService;
        this.playbackService = playbackService;
        this.metrics = metrics;
    }

    @GetMapping(value = {"/Song/Search", "/api/Song/Search"})
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam(value="q", required=false) String q) {
        List<Map<String, Object>> results = songSearchService.searchSongs(q);
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = {"/Song/Play/{id}", "/api/Song/Play/{id}"})
    public ResponseEntity<Map<String, Object>> playSong(@PathVariable("id") Long id) {
        var user = tr.com.huseyinaydin.web.security.CurrentUser.get().orElse(null);
        if (user == null) {
            throw new tr.com.huseyinaydin.sharedkernel.exception.AuthorizationException("playSong", "Müzik dinlemek için giriş yapmalısınız.");
        }

        String tier = user.tier();
        
        // Open-Closed Principle (OCP): Yetki doğrulaması soyutlandı.
        if (!playbackService.hasAccess(id, tier)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Bu şarkı için en az " + playbackService.getRequiredTierLevel(id) + " paketine sahip olmanız gerekiyor.");
        }

        // Open-Closed Principle (OCP): Hardcoded URL dizisi soyutlandı.
        String audioUrl = playbackService.getAudioUrl(id);
        playbackService.recordListenEvent(user.email(), id);
        
        Map<String, Object> song = (songSearchService != null) ? songSearchService.getSongById(id) : null;
        String title = (song != null && song.get("title") != null) ? String.valueOf(song.get("title")) : ("Song " + id);
        String artist = (song != null && song.get("artist") != null) ? String.valueOf(song.get("artist")) : "";

        Map<String, Object> response = new HashMap<>();
        response.put("audioUrl", audioUrl);
        response.put("title", title);
        response.put("artist", artist);
        
        if (metrics != null) {
            metrics.incrementSongsPlayed(tier);
        }
        return ResponseEntity.ok(response);
    }
}
