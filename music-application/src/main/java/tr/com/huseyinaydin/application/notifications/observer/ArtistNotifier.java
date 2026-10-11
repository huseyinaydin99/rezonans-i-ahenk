package tr.com.huseyinaydin.application.notifications.observer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.huseyinaydin.domain.artist.Artist;
import tr.com.huseyinaydin.domain.song.Song;

import java.util.ArrayList;
import java.util.List;

public class ArtistNotifier implements ISubject {
    private static final Logger log = LoggerFactory.getLogger(ArtistNotifier.class);


    private final Artist artist;
    private final List<IObserver> followers;

    public ArtistNotifier(Artist artist) {
        this.artist = artist;
        this.followers = new ArrayList<>();
    }

    @Override
    public void attach(IObserver observer) {
        if (!followers.contains(observer)) {
            followers.add(observer);
            log.info("[TAKİP] Yeni bir kullanıcı '" + artist.getName() + "' isimli sanatçıyı takibe aldı.");
        }
    }

    @Override
    public void detach(IObserver observer) {
        followers.remove(observer);
        log.info("[TAKİBİ BIRAK] Bir kullanıcı '" + artist.getName() + "' isimli sanatçıyı takipten çıkardı.");
    }

    @Override
    public void notifyObservers(Song newSong) {
        log.info("\n[SİSTEM] '" + artist.getName() + "' yeni bir şarkı yayınladı. Tüm takipçilere (Gözlemcilere) haber veriliyor...");
        for (IObserver follower : followers) {
            // Sanatçı nesnesi, takipçilerin detaylarını bilmez. Sadece onlara "yeni şarkıyı" iletir (Loose Coupling).
            follower.update(artist.getName(), newSong);
        }
    }

    /**
     * İş kuralı: Sanatçı sisteme yeni bir şarkı eklediğinde otomatik olarak Observer ağı tetiklenir.
     */
    public void releaseNewSong(Song newSong) {
        // Şarkı kaydetme iş kuralları...
        notifyObservers(newSong);
    }
}
