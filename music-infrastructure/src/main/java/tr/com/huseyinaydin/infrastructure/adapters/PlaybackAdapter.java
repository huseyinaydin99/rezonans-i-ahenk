package tr.com.huseyinaydin.infrastructure.adapters;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IPlaybackService;
import tr.com.huseyinaydin.infrastructure.kafka.ListenEventProducer;

import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@ConfigurationProperties(prefix = "music.tiers")
public class PlaybackAdapter implements IPlaybackService {

    private final ListenEventProducer listenEventProducer;

    @Value("${music.playback.urls}")
    private List<String> urls;

    // application.properties'ten (music.tiers.levels.*) çekilen veriler
    private Map<String, Integer> levels = new HashMap<>();

    // application.properties'ten (music.tiers.songs.*) çekilen veriler
    private Map<String, String> songs = new HashMap<>();

    public PlaybackAdapter(ListenEventProducer listenEventProducer) {
        this.listenEventProducer = listenEventProducer;
    }

    public Map<String, Integer> getLevels() { return levels; }
    public void setLevels(Map<String, Integer> levels) { this.levels = levels; }

    public Map<String, String> getSongs() { return songs; }
    public void setSongs(Map<String, String> songs) { this.songs = songs; }

    @Override
    public void recordListenEvent(String userId, Long songId) {
        CompletableFuture.runAsync(() -> {
            listenEventProducer.sendListenEvent(userId, songId);
        });
    }

    @Override
    public String getAudioUrl(Long songId) {
        if (urls == null || urls.isEmpty()) {
            return "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
        }
        int index = (int) (songId % urls.size());
        return urls.get(index);
    }

    @Override
    public boolean hasAccess(Long songId, String userTier) {
        if (userTier == null) userTier = "Basic";
        
        Integer userLevel = levels.getOrDefault(userTier, 0);
        Integer requiredLevel = getRequiredTierLevel(songId);

        return userLevel >= requiredLevel;
    }

    @Override
    public Integer getRequiredTierLevel(Long songId) {
        String requiredTierName = songs.get(String.valueOf(songId));
        if (requiredTierName == null) return 0; // Default is Basic (0)
        return levels.getOrDefault(requiredTierName, 0);
    }
}
