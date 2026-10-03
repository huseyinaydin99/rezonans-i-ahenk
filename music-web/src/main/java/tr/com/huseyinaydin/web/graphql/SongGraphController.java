package tr.com.huseyinaydin.web.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.domain.artist.Artist;
import tr.com.huseyinaydin.domain.enums.MembershipTier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * REST uç noktalarının alternatifi olan ve Client'ın tam olarak ne istediğini 
 * seçebildiği (Over-fetching engelleyici) GraphQL kontrolcüsü.
 */
@Controller
public class SongGraphController {

    @QueryMapping
    public Song getSongById(@Argument String id) {
        System.out.println("[GRAPHQL] 'getSongById' sorgusu alındı. Sadece istemcinin istediği alanlar dönülecek.");
        // Simüle edilmiş veritabanı / CQRS sorgusu
        return Song.builder()
                .id(UUID.fromString(id))
                .title("GraphQL ile Gelen Şarkı")
                .audioUrl("http://minio/song.mp3")
                .duration(Duration.ofSeconds(180))
                .tier(MembershipTier.BASIC)
                .artistId(UUID.randomUUID())
                .genreId(UUID.randomUUID())
                .build();
    }

    @QueryMapping
    public List<Song> searchSongs(@Argument String keyword) {
        System.out.println("[GRAPHQL] '" + keyword + "' araması yapılıyor... (Elasticsearch motoruna yönlendirilecek)");
        return new ArrayList<>();
    }

    /**
     * Alt graf (Sub-graph) çözümlemesi: Şarkının sanatçı bilgisi istendiyse bu metot tetiklenir.
     * İstenmediyse (örneğin sadece şarkı adı çekiliyorsa) bu metot hiç çalışmaz (Performans optimizasyonu).
     */
    @SchemaMapping(typeName = "Song", field = "artist")
    public Artist getArtistForSong(Song song) {
        System.out.println("[GRAPHQL] Alt graf (Artist) istendiği için Sanatçı veritabanından çekiliyor...");
        return Artist.builder()
                .id(song.getId()) // Normalde song.getArtistId() olmalı
                .name("Harika Sanatçı")
                .build();
    }
}
