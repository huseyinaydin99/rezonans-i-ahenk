package tr.com.huseyinaydin.web.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
import tr.com.huseyinaydin.application.ports.ITokenService;
import tr.com.huseyinaydin.application.ports.TokenClaims;
import tr.com.huseyinaydin.infrastructure.security.JwtAuthenticationFilter;
import tr.com.huseyinaydin.web.security.CurrentUser;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping({"/web-api/playlists", "/api/v1/playlists"})
public class PlaylistsController extends BaseController {

    private final IPlaylistService playlistService;
    private final ITokenService tokenService;

    public PlaylistsController(Mediator mediator,
                               @Autowired(required = false) IPlaylistService playlistService,
                               @Autowired(required = false) ITokenService tokenService) {
        super(mediator);
        this.playlistService = playlistService;
        this.tokenService = tokenService;
    }

    private String resolveUserEmail(HttpServletRequest request) {
        String email = CurrentUser.get().map(TokenClaims::email).orElse(null);
        if (email != null && !email.isBlank()) {
            return email;
        }
        if (request != null && request.getCookies() != null && tokenService != null) {
            for (Cookie c : request.getCookies()) {
                if (JwtAuthenticationFilter.AUTH_COOKIE_NAME.equals(c.getName())) {
                    Optional<TokenClaims> claims = tokenService.parseAccessToken(c.getValue());
                    if (claims.isPresent()) {
                        return claims.get().email();
                    }
                }
            }
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getPlaylists(HttpServletRequest request) {
        String email = resolveUserEmail(request);
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

    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistDetailDto> getPlaylistDetail(@PathVariable("playlistId") String playlistId) {
        if (playlistService != null) {
            PlaylistDetailDto p = playlistService.getPlaylistById(playlistId);
            if (p != null) {
                return ResponseEntity.ok(p);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        String email = resolveUserEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String name = (String) payload.get("name");
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String trimmedName = name.trim();
        String description = (String) payload.getOrDefault("description", "");

        if (playlistService != null) {
            List<PlaylistDetailDto> existing = playlistService.getUserPlaylists(email);
            for (PlaylistDetailDto p : existing) {
                if (p.getName() != null && p.getName().trim().equalsIgnoreCase(trimmedName)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
            CreatePlaylistDto dto = new CreatePlaylistDto();
            dto.setUserId(email);
            dto.setName(trimmedName);
            dto.setDescription(description);
            try {
                playlistService.createPlaylist(dto);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }

        UUID userUuid = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
        UUID playlistUuid;
        try {
            playlistUuid = mediator.send(new CreatePlaylistCommand(trimmedName, userUuid));
        } catch (Exception e) {
            playlistUuid = UUID.randomUUID();
        }

        return ResponseEntity.ok(playlistUuid);
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Boolean> addSong(@PathVariable("playlistId") String playlistId,
                                           @PathVariable("songId") String songId,
                                           HttpServletRequest request) {
        String email = resolveUserEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean added = false;
        if (playlistService != null) {
            added = playlistService.addSongToPlaylist(playlistId, songId);
        }

        if (!added) {
            // Şarkı bu listede zaten mevcut!
            return ResponseEntity.status(HttpStatus.CONFLICT).body(false);
        }

        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Boolean> removeSong(@PathVariable("playlistId") String playlistId,
                                              @PathVariable("songId") String songId,
                                              HttpServletRequest request) {
        if (playlistService != null) {
            playlistService.removeSongFromPlaylist(playlistId, songId);
        }
        return ResponseEntity.ok(true);
    }

    @PostMapping("/{playlistId}/share")
    public ResponseEntity<String> share(@PathVariable("playlistId") String playlistId) {
        UUID pId;
        try { pId = UUID.fromString(playlistId); } catch (Exception e) { pId = UUID.nameUUIDFromBytes(playlistId.getBytes(StandardCharsets.UTF_8)); }
        return ResponseEntity.ok(mediator.send(new SharePlaylistCommand(pId)));
    }
}
