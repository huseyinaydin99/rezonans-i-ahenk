package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tr.com.huseyinaydin.application.playback.PlaybackTrackingService;
import tr.com.huseyinaydin.application.playback.ReportPlaybackCommand;
import tr.com.huseyinaydin.domain.playback.PlaybackState;
import tr.com.huseyinaydin.domain.playback.events.SongPlaybackReportedEvent;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

/**
 * Global oynatıcının nabız (heartbeat) bildirimlerini karşılayan uç nokta.
 * Oynatıcı çalarken periyodik olarak ve her durum geçişinde (başla, duraklat, atla, bitir) buraya yazar.
 */
@RestController
@RequestMapping("/api/v1/playback")
public class PlaybackController {

    private final PlaybackTrackingService playbackTrackingService;

    public PlaybackController(PlaybackTrackingService playbackTrackingService) {
        this.playbackTrackingService = playbackTrackingService;
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<PlaybackHeartbeatResponse> heartbeat(@RequestBody PlaybackHeartbeatRequest request,
                                                               Principal principal) {
        Optional<UUID> userId = resolveUserId(principal, request.userId());
        if (userId.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        SongPlaybackReportedEvent event = playbackTrackingService.report(new ReportPlaybackCommand(
                userId.get(),
                request.songId(),
                request.state(),
                request.positionSeconds(),
                request.durationSeconds()
        ));

        return ResponseEntity.accepted()
                .body(new PlaybackHeartbeatResponse(event.getEventId(), event.isQualifiedListen()));
    }

    /**
     * Kimlik önce doğrulanmış token'ın öznesinden (subject) alınır.
     * NOT: Mevcut JwtAuthenticationFilter henüz gerçek kullanıcı ID'sini principal'a koymadığı için,
     * geçici olarak istemcinin gönderdiği userId'ye düşülür. Filtre gerçek subject taşıdığında
     * bu fallback kaldırılmalıdır.
     */
    private Optional<UUID> resolveUserId(Principal principal, UUID fallbackUserId) {
        if (principal != null) {
            try {
                return Optional.of(UUID.fromString(principal.getName()));
            } catch (IllegalArgumentException ignored) {
                // principal UUID değil -> fallback
            }
        }
        return Optional.ofNullable(fallbackUserId);
    }

    public record PlaybackHeartbeatRequest(
            UUID userId,
            UUID songId,
            PlaybackState state,
            long positionSeconds,
            long durationSeconds
    ) { }

    public record PlaybackHeartbeatResponse(UUID eventId, boolean qualifiedListen) { }
}
