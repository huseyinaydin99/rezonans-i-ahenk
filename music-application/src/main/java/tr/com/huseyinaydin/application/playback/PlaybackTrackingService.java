package tr.com.huseyinaydin.application.playback;

import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.domain.playback.events.SongPlaybackReportedEvent;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEventDispatcher;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

/**
 * Oynatıcıdan gelen bildirimleri doğrulayıp domain olayına çevirir ve
 * soyut dağıtıcıya (IDomainEventDispatcher -> Kafka) teslim eder.
 * Bu servis Kafka'yı tanımaz; Onion Architecture bağımlılık yönü korunur.
 */
@Service
public class PlaybackTrackingService {

    /** İstemci saatine körü körüne güvenmemek için makul üst sınır (6 saat). */
    private static final long MAX_REASONABLE_SECONDS = 6 * 60 * 60;

    private final IDomainEventDispatcher eventDispatcher;

    public PlaybackTrackingService(IDomainEventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public SongPlaybackReportedEvent report(ReportPlaybackCommand command) {
        validate(command);

        SongPlaybackReportedEvent event = new SongPlaybackReportedEvent(
                command.userId(),
                command.songId(),
                command.state(),
                command.positionSeconds(),
                command.durationSeconds()
        );

        eventDispatcher.dispatch(event);
        return event;
    }

    private void validate(ReportPlaybackCommand command) {
        if (command.userId() == null || command.songId() == null || command.state() == null) {
            throw new BusinessException("Eksik dinleme bildirimi: kullanıcı, şarkı ve durum zorunludur.");
        }
        if (command.positionSeconds() < 0 || command.positionSeconds() > MAX_REASONABLE_SECONDS
                || command.durationSeconds() < 0 || command.durationSeconds() > MAX_REASONABLE_SECONDS) {
            throw new BusinessException("Geçersiz oynatma süresi bildirildi.");
        }
    }
}
