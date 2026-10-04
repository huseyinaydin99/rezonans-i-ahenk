package tr.com.huseyinaydin.application.playback;

import tr.com.huseyinaydin.domain.playback.PlaybackState;

import java.util.UUID;

/**
 * Oynatıcının sunucuya gönderdiği nabız (heartbeat) komutu.
 */
public record ReportPlaybackCommand(
        UUID userId,
        UUID songId,
        PlaybackState state,
        long positionSeconds,
        long durationSeconds
) { }
