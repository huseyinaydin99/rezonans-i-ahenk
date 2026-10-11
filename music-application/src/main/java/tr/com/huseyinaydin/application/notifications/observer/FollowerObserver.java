package tr.com.huseyinaydin.application.notifications.observer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.huseyinaydin.domain.song.Song;
import java.util.UUID;

public class FollowerObserver implements IObserver {
    private static final Logger log = LoggerFactory.getLogger(FollowerObserver.class);

    private final UUID userId;
    private final String username;

    public FollowerObserver(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public void update(String artistName, Song newSong) {
        // Burada gerçek bir senaryoda WebSocket veya Email bildirimi tetiklenir
        log.info("[BİLDİRİM AĞI] Sayın " + username + " (ID: " + userId + "), " +
                "Takip ettiğiniz sanatçı '" + artistName + "' yeni bir şarkı yayınladı: " + newSong.getTitle());
    }

    public UUID getUserId() { return userId; }
}
