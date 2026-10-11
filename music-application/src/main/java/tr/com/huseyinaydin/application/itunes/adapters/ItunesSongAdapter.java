package tr.com.huseyinaydin.application.itunes.adapters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesTrackDto;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.domain.enums.MembershipTier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ItunesSongAdapter {
    private static final Logger log = LoggerFactory.getLogger(ItunesSongAdapter.class);


    /**
     * Dış dünyadan (iTunes) gelen uyumsuz ItunesTrackDto nesnesini, 
     * mimarimizin saf (Pure) çekirdek varlığı olan Song nesnesine dönüştürür.
     */
    public Song adaptToDomainSong(ItunesTrackDto externalTrack) {
        
        if (externalTrack == null) {
            throw new IllegalArgumentException("Dış veri kaynağı (iTunes Track) boş olamaz!");
        }

        log.info("[ADAPTER] Dış iTunes verisi iç mimarimize (Song) entegre ediliyor...");

        // iTunes süresi milisaniye cinsinden geliyor, Duration'a çeviriyoruz
        Duration duration = Duration.ofMillis(externalTrack.trackTimeMillis());

        // Dış API'de sanatçı ve tür ID'si doğrudan gelmediği veya farklı formatta geldiği için
        // burada kendi sistemimize uyarlamak adına varsayılan UUID'ler (veya veritabanı araması) yapılıyor.
        // Mimari gösterim amacı taşıdığından rastgele UUID atanmıştır.
        UUID mappedArtistId = UUID.randomUUID(); 
        UUID mappedGenreId = UUID.randomUUID();

        // Önceki adımda tasarladığımız Builder (İnşacı) şablonuyla nesnemizi kusursuzca üretiyoruz!
        return Song.builder()
                .title(externalTrack.trackName())
                .coverImageUrl(externalTrack.artworkUrl100())
                .audioUrl(externalTrack.previewUrl())
                .duration(duration)
                .tier(MembershipTier.BASIC) // Dışarıdan gelenleri varsayılan olarak Basic yap
                .releaseDate(LocalDateTime.now()) // iTunes API'sinden çekilen tarihi parse edebiliriz
                .artistId(mappedArtistId)
                .genreId(mappedGenreId)
                .build();
    }
}
