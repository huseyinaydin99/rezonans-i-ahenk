package tr.com.huseyinaydin.infrastructure.adapters;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${music.playback.urls:https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3,https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3,https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3,https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3,https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3}")
    private List<String> urls;

    // application.properties'ten (music.tiers.levels.*) çekilen veriler
    private Map<String, Integer> levels = new HashMap<>() {{
        put("Basic", 0);
        put("Gold", 1);
        put("Premium", 2);
        put("Elit", 3);
    }};

    // application.properties'ten (music.tiers.songs.*) çekilen veriler
    private Map<String, String> songs = new HashMap<>() {{
        put("3", "Premium");
        put("4", "Premium");
        put("5", "Elit");
    }};

    private final tr.com.huseyinaydin.application.ports.ISongSearchService songSearchService;

    public PlaybackAdapter(ListenEventProducer listenEventProducer,
                           @org.springframework.context.annotation.Lazy @Autowired(required = false) tr.com.huseyinaydin.application.ports.ISongSearchService songSearchService) {
        this.listenEventProducer = listenEventProducer;
        this.songSearchService = songSearchService;
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
        if (songSearchService != null && songId != null) {
            Map<String, Object> song = songSearchService.getSongById(songId);
            if (song != null && song.get("tier") != null) {
                return levels.getOrDefault(String.valueOf(song.get("tier")), 0);
            }
        }
        String requiredTierName = songs.get(String.valueOf(songId));
        if (requiredTierName == null) return 0; // Default is Basic (0)
        return levels.getOrDefault(requiredTierName, 0);
    }
}
