package tr.com.huseyinaydin.persistence.seed;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.domain.artist.Artist;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import tr.com.huseyinaydin.domain.genre.Genre;
import tr.com.huseyinaydin.domain.repositories.IArtistRepository;
import tr.com.huseyinaydin.domain.repositories.IGenreRepository;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.domain.song.Song;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class MusicDataSeeder {

    private final IArtistRepository artistRepository;
    private final IGenreRepository genreRepository;
    private final ISongRepository songRepository;

    public MusicDataSeeder(IArtistRepository artistRepository, IGenreRepository genreRepository, ISongRepository songRepository) {
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
        this.songRepository = songRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedData() {
        if (genreRepository.count(null) == 0) {
            Genre rock = new Genre("Rock", "rock.jpg");
            Genre pop = new Genre("Pop", "pop.jpg");
            genreRepository.save(rock);
            genreRepository.save(pop);

            Artist artist = new Artist("The Legendary Band", "band.jpg", "An epic rock band");
            artistRepository.save(artist);

            Song song1 = new Song("Epic Journey", "cover1.jpg", "audio1.mp3", Duration.ofMinutes(4),
                    MembershipTier.BASIC, LocalDateTime.now(), artist.getId(), rock.getId());
            Song song2 = new Song("Pop Anthem", "cover2.jpg", "audio2.mp3", Duration.ofMinutes(3),
                    MembershipTier.PREMIUM, LocalDateTime.now(), artist.getId(), pop.getId());
            
            songRepository.save(song1);
            songRepository.save(song2);
            
            System.out.println("Music platform initial data seeded successfully!");
        }
    }
}
