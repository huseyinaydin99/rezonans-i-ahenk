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

@Component("csvImporter")
public class CsvBulkImporter extends BaseBulkImporter {
    private static final Logger log = LoggerFactory.getLogger(CsvBulkImporter.class);


    @Override
    protected List<Song> parseData(String rawContent) {
        log.info("[ADIM 2] Ham veri CSV (Virgülle Ayrılmış) algoritmasına göre satır satır ayrıştırılıyor...");
        
        // Simüle edilmiş CSV ayrıştırma işlemi (Önceki aşamalardaki Builder kalıbını kullanıyoruz)
        List<Song> songs = new ArrayList<>();
        songs.add(Song.builder()
                .title("CSV Mock Song")
                .audioUrl("http://cdn/csv_song.mp3")
                .duration(Duration.ofSeconds(180))
                .tier(MembershipTier.BASIC)
                .releaseDate(LocalDateTime.now())
                .artistId(UUID.randomUUID())
                .genreId(UUID.randomUUID())
                .build());
                
        return songs;
    }
}
