package tr.com.huseyinaydin.application.importing;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("jsonImporter")
public class JsonBulkImporter extends BaseBulkImporter {
    private static final Logger log = LoggerFactory.getLogger(JsonBulkImporter.class);


    @Override
    protected List<Song> parseData(String rawContent) {
        log.info("[ADIM 2] Ham veri JSON formatına göre Deserializer (Jackson/Gson) ile ayrıştırılıyor...");
        
        // Simüle edilmiş JSON ayrıştırma işlemi
        List<Song> songs = new ArrayList<>();
        songs.add(Song.builder()
                .title("JSON Mock Song")
                .audioUrl("http://cdn/json_song.mp3")
                .duration(Duration.ofSeconds(210))
                .tier(MembershipTier.PREMIUM)
                .releaseDate(LocalDateTime.now())
                .artistId(UUID.randomUUID())
                .genreId(UUID.randomUUID())
                .build());
                
        return songs;
    }
}
