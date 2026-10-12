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
import tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto;
import tr.com.huseyinaydin.application.playlist.queries.GetPlaylistByIdQuery;
import tr.com.huseyinaydin.application.playlist.queries.GetUserPlaylistsQuery;
import tr.com.huseyinaydin.application.ports.ITokenService;
import tr.com.huseyinaydin.application.ports.TokenClaims;
import tr.com.huseyinaydin.infrastructure.security.JwtAuthenticationFilter;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;
import tr.com.huseyinaydin.web.security.CurrentUser;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping({"/web-api/playlists", "/api/v1/playlists"})
public class PlaylistsController extends BaseController {

    private final ITokenService tokenService;

    public PlaylistsController(Mediator mediator,
                               @Autowired(required = false) ITokenService tokenService) {
        super(mediator);
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

        List<PlaylistDetailDto> list = mediator.query(new GetUserPlaylistsQuery(email));
        if (list == null) {
            list = Collections.emptyList();
        }

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
        PlaylistDetailDto p = mediator.query(new GetPlaylistByIdQuery(playlistId));
        if (p != null) {
            return ResponseEntity.ok(p);
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

        try {
            UUID playlistUuid = mediator.send(new CreatePlaylistCommand(trimmedName, description, email));
            return ResponseEntity.ok(playlistUuid);
        } catch (BusinessException be) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Boolean> addSong(@PathVariable("playlistId") String playlistId,
                                           @PathVariable("songId") String songId,
                                           HttpServletRequest request) {
        String email = resolveUserEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Boolean added = mediator.send(new AddSongToPlaylistCommand(playlistId, songId));
        if (added == null || !added) {
            // Şarkı bu listede zaten mevcut!
            return ResponseEntity.status(HttpStatus.CONFLICT).body(false);
        }

        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Boolean> removeSong(@PathVariable("playlistId") String playlistId,
                                              @PathVariable("songId") String songId,
                                              HttpServletRequest request) {
        Boolean removed = mediator.send(new RemoveSongFromPlaylistCommand(playlistId, songId));
        return ResponseEntity.ok(removed != null ? removed : true);
    }

    @PostMapping("/{playlistId}/share")
    public ResponseEntity<String> share(@PathVariable("playlistId") String playlistId) {
        UUID pId;
        try {
            pId = UUID.fromString(playlistId);
        } catch (Exception e) {
            pId = UUID.nameUUIDFromBytes(playlistId.getBytes(StandardCharsets.UTF_8));
        }
        return ResponseEntity.ok(mediator.send(new SharePlaylistCommand(pId)));
    }
}
