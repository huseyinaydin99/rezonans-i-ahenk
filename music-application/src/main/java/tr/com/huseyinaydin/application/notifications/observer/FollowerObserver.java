package tr.com.huseyinaydin.application.notifications.observer;

import tr.com.huseyinaydin.domain.song.Song;
import java.util.UUID;

public class FollowerObserver implements IObserver {
    private final UUID userId;
    private final String username;

    public FollowerObserver(UUID userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public void update(String artistName, Song newSong) {
        // Burada gerçek bir senaryoda WebSocket veya Email bildirimi tetiklenir
        System.out.println("[BİLDİRİM AĞI] Sayın " + username + " (ID: " + userId + "), " +
                "Takip ettiğiniz sanatçı '" + artistName + "' yeni bir şarkı yayınladı: " + newSong.getTitle());
    }

    public UUID getUserId() { return userId; }
}
