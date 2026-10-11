package tr.com.huseyinaydin.application.genre.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.ports.IGenreService;
import tr.com.huseyinaydin.application.genre.dtos.GenreBrowseDto;
import tr.com.huseyinaydin.application.genre.dtos.GenreDetailDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GenreServiceImpl implements IGenreService {
    private static final Logger log = LoggerFactory.getLogger(GenreServiceImpl.class);


    @Override
    @Cacheable(value = "genresCache", key = "'all'")
    public List<GenreBrowseDto> getAllGenres() {
        log.info("[KEŞFET (BROWSE)] Tüm müzik türleri (Genres) Hazelcast önbelleğinden (Cache) çekiliyor...");
        
        List<GenreBrowseDto> genres = new ArrayList<>();
        genres.add(new GenreBrowseDto("rock-id", "Rock", "https://minio/rock.jpg", "#FF5733"));
        genres.add(new GenreBrowseDto("pop-id", "Pop", "https://minio/pop.jpg", "#FFC300"));
        genres.add(new GenreBrowseDto("jazz-id", "Jazz", "https://minio/jazz.jpg", "#900C3F"));
        genres.add(new GenreBrowseDto("classical-id", "Klasik", "https://minio/classical.jpg", "#581845"));
        genres.add(new GenreBrowseDto("hiphop-id", "Hip-Hop", "https://minio/hiphop.jpg", "#196F3D"));
        genres.add(new GenreBrowseDto("electro-id", "Elektronik", "https://minio/electro.jpg", "#2874A6"));
        
        return genres;
    }

    @Override
    @Cacheable(value = "genresCache", key = "#genreId")
    public GenreDetailDto getTopSongsByGenreId(String genreId) {
        log.info("[ELASTICSEARCH] '" + genreId + "' türündeki en popüler şarkılar indekslerden ışık hızında getiriliyor...");
        
        List<String> mockSongs = new ArrayList<>();
        if(genreId.equals("rock-id")) {
            mockSongs.add("Nirvana - Smells Like Teen Spirit");
            mockSongs.add("Guns N' Roses - Sweet Child O' Mine");
            mockSongs.add("AC/DC - Back In Black");
            return new GenreDetailDto("Rock Efsaneleri", mockSongs);
        }
        else if (genreId.equals("jazz-id")) {
            mockSongs.add("Miles Davis - So What");
            mockSongs.add("Louis Armstrong - What A Wonderful World");
            return new GenreDetailDto("Caz Rüzgarı", mockSongs);
        }
        
        // Varsayılan
        mockSongs.add("Bilinmeyen Sanatçı - Harika Parça");
        return new GenreDetailDto("Karışık Kategori", mockSongs);
    }
}
