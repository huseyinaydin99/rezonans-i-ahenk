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

    @Autowired
    public SongController(ISongSearchService songSearchService, IPlaybackService playbackService) {
        this.songSearchService = songSearchService;
        this.playbackService = playbackService;
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
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Müzik dinlemek için giriş yapmalısınız.");
            return ResponseEntity.status(401).body(err);
        }

        String tier = user.tier();
        
        // Open-Closed Principle (OCP): Yetki doğrulaması soyutlandı.
        if (!playbackService.hasAccess(id, tier)) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Bu şarkı için en az daha üst bir pakete sahip olmanız gerekiyor.");
            err.put("requiredTier", playbackService.getRequiredTierLevel(id));
            return ResponseEntity.status(403).body(err);
        }

        // Open-Closed Principle (OCP): Hardcoded URL dizisi soyutlandı.
        String audioUrl = playbackService.getAudioUrl(id);
        playbackService.recordListenEvent(user.email(), id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("audioUrl", audioUrl);
        response.put("title", "Song " + id);
        
        return ResponseEntity.ok(response);
    }
}
