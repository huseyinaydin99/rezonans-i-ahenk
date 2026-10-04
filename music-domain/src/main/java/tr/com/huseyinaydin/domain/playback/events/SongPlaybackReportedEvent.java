package tr.com.huseyinaydin.domain.playback.events;

import tr.com.huseyinaydin.domain.playback.ListenQualificationPolicy;
import tr.com.huseyinaydin.domain.playback.PlaybackState;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEvent;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Oynatıcıdan gelen her durum bildirimini temsil eden değişmez (immutable) domain olayı.
 * Kafka üzerinden Cassandra (dinleme geçmişi), Spark (ML) ve WebSocket tüketicilerine akar.
 */
public final class SongPlaybackReportedEvent implements IDomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredOn;
    private final UUID userId;
    private final UUID songId;
    private final PlaybackState state;
    private final long positionSeconds;
    private final long durationSeconds;
    private final boolean qualifiedListen;

    public SongPlaybackReportedEvent(UUID userId, UUID songId, PlaybackState state,
                                     long positionSeconds, long durationSeconds) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = LocalDateTime.now();
        this.userId = Objects.requireNonNull(userId, "userId");
        this.songId = Objects.requireNonNull(songId, "songId");
        this.state = Objects.requireNonNull(state, "state");
        this.positionSeconds = Math.max(0, positionSeconds);
        this.durationSeconds = Math.max(0, durationSeconds);
        this.qualifiedListen = ListenQualificationPolicy.qualifies(this.positionSeconds, this.durationSeconds);
    }

    @Override public UUID getEventId() { return eventId; }
    @Override public LocalDateTime getOccurredOn() { return occurredOn; }
    public UUID getUserId() { return userId; }
    public UUID getSongId() { return songId; }
    public PlaybackState getState() { return state; }
    public long getPositionSeconds() { return positionSeconds; }
    public long getDurationSeconds() { return durationSeconds; }
    public boolean isQualifiedListen() { return qualifiedListen; }

    /** Dinleme geçmişine yazılması gereken olay mı? (Oturum bitti ve eşik aşıldı.) */
    public boolean shouldBeRecordedInHistory() {
        return state.isTerminal() && qualifiedListen;
    }
}
