package tr.com.huseyinaydin.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.playlist.commands.CreatePlaylistCommand;
import tr.com.huseyinaydin.application.playlist.commands.AddSongToPlaylistCommand;
import tr.com.huseyinaydin.application.playlist.commands.RemoveSongFromPlaylistCommand;
import tr.com.huseyinaydin.application.playlist.commands.SharePlaylistCommand;
import tr.com.huseyinaydin.application.playlist.dtos.CreatePlaylistDto;
import tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.web.security.CurrentUser;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistsController extends BaseController {

    private final IPlaylistService playlistService;

    public PlaylistsController(Mediator mediator, @Autowired(required = false) IPlaylistService playlistService) {
        super(mediator);
        this.playlistService = playlistService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getPlaylists() {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PlaylistDetailDto> list = (playlistService != null)
                ? playlistService.getUserPlaylists(email)
                : Collections.emptyList();

        List<Map<String, Object>> response = new ArrayList<>();
        for (PlaylistDetailDto p : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("playlistId", p.getId());
            map.put("name", p.getName());
            map.put("description", p.getDescription());
            map.put("songCount", p.getSongCount());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody Map<String, Object> payload) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String name = (String) payload.get("name");
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String description = (String) payload.getOrDefault("description", "");

        UUID userUuid = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        UUID playlistUuid;
        try {
            playlistUuid = mediator.send(new CreatePlaylistCommand(name.trim(), userUuid));
        } catch (Exception e) {
            playlistUuid = UUID.randomUUID();
        }

        if (playlistService != null) {
            CreatePlaylistDto dto = new CreatePlaylistDto();
            dto.setUserId(email);
            dto.setName(name.trim());
            dto.setDescription(description);
            playlistService.createPlaylist(dto);
        }

        return ResponseEntity.ok(playlistUuid);
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Boolean> addSong(@PathVariable String playlistId, @PathVariable String songId) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (playlistService != null) {
            playlistService.addSongToPlaylist(playlistId, songId);
        }

        try {
            UUID pId;
            try { pId = UUID.fromString(playlistId); } catch (Exception e) { pId = UUID.nameUUIDFromBytes(playlistId.getBytes(StandardCharsets.UTF_8)); }
            UUID sId;
            try { sId = UUID.fromString(songId); } catch (Exception e) { sId = UUID.nameUUIDFromBytes(songId.getBytes(StandardCharsets.UTF_8)); }
            mediator.send(new AddSongToPlaylistCommand(pId, sId));
        } catch (Exception ignored) {}

        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Boolean> removeSong(@PathVariable String playlistId, @PathVariable String songId) {
        if (playlistService != null) {
            playlistService.removeSongFromPlaylist(playlistId, songId);
        }
        return ResponseEntity.ok(true);
    }

    @PostMapping("/{playlistId}/share")
    public ResponseEntity<String> share(@PathVariable String playlistId) {
        UUID pId;
        try { pId = UUID.fromString(playlistId); } catch (Exception e) { pId = UUID.nameUUIDFromBytes(playlistId.getBytes(StandardCharsets.UTF_8)); }
        return ResponseEntity.ok(mediator.send(new SharePlaylistCommand(pId)));
    }
}
