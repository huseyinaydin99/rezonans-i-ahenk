package tr.com.huseyinaydin.application.like.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.ILikeService;
import tr.com.huseyinaydin.application.like.dtos.LikeToggleDto;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEventDispatcher;
// import tr.com.huseyinaydin.domain.song.events.SongLikedEvent; (Simülasyon)

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LikeServiceImpl implements ILikeService {

    private final IDomainEventDispatcher eventDispatcher;

    public LikeServiceImpl(IDomainEventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    @Transactional
    public boolean toggleLike(LikeToggleDto dto) {
        System.out.println("[BEĞENİ SİSTEMİ] Kullanıcı (" + dto.getUserId() + "), Şarkı (" + dto.getSongId() + ") etkileşimi inceleniyor...");
        
        // Simülasyon: Veritabanında (SongLike entity) kayıt var mı kontrolü
        boolean isAlreadyLiked = Math.random() > 0.5; // Rastgele toggle
        
        if (isAlreadyLiked) {
            System.out.println("-> Şarkı beğenilerden çıkarıldı (UNLIKE).");
            // repository.deleteByUserIdAndSongId(...)
            return false;
        } else {
            System.out.println("-> Şarkı favorilere eklendi (LIKE). Kafka Event fırlatılıyor...");
            // repository.save(new SongLike(...))
            
            // Olay yönelimli mimari (Kafka/Outbox üzerinden ML motorunu besler)
            // eventDispatcher.dispatch(new SongLikedEvent(dto.getSongId(), dto.getUserId()));
            return true;
        }
    }

    @Override
    public List<String> getLikedSongs(UUID userId) {
        // Gerçekte: repository.findSongsByUserId(userId)
        List<String> mockList = new ArrayList<>();
        mockList.add("Queen - Bohemian Rhapsody");
        mockList.add("The Weeknd - Blinding Lights");
        return mockList;
    }
}
