package tr.com.huseyinaydin.web.beans;

import jakarta.annotation.PostConstruct;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.ILikeService;
import tr.com.huseyinaydin.application.like.dtos.LikeToggleDto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@SessionScope
public class LikeBean implements Serializable {

    @Autowired
    private ILikeService likeService;

    // YENİ: Kullanıcının beğendiği tüm şarkıların ID'lerini hafızada (Oturumda) tutar.
    // Bu sayede herhangi bir sayfada (Global) kalp ikonunun dolu/boş olacağı O(1) hızında kontrol edilir.
    private Set<String> userLikedSongs = new HashSet<>();
    private UUID currentUserId = UUID.fromString("00000000-0000-0000-0000-000000000001"); // Mock User ID

    @PostConstruct
    public void init() {
        loadLikedSongs();
    }

    public void loadLikedSongs() {
        List<String> likedIds = likeService.getLikedSongs(currentUserId);
        if (likedIds != null) {
            this.userLikedSongs = new HashSet<>(likedIds);
        }
    }

    public void toggleFavorite(String songIdStr) {
        if (songIdStr == null || songIdStr.isEmpty()) return;
        
        UUID songId = UUID.fromString(songIdStr);
        LikeToggleDto dto = new LikeToggleDto(currentUserId, songId);
        
        // Servis bize şarkının yeni durumunu (Beğenildi mi / Geri mi alındı) döner
        boolean isNowLiked = likeService.toggleLike(dto);
        
        // Global UI state'i anında güncelle (Sayfa yenilemeye gerek kalmadan)
        if (isNowLiked) {
            this.userLikedSongs.add(songIdStr);
            System.out.println("[JSF AJAX] Şarkı FAVORİLERE EKLENDİ. (ID: " + songIdStr + ")");
        } else {
            this.userLikedSongs.remove(songIdStr);
            System.out.println("[JSF AJAX] Şarkı FAVORİLERDEN ÇIKARILDI. (ID: " + songIdStr + ")");
        }
    }

    // JSF EL (Expression Language) üzerinden herhangi bir şarkının beğenilip beğenilmediğini sorgular
    public boolean isSongLiked(String songIdStr) {
        if (songIdStr == null) return false;
        return userLikedSongs.contains(songIdStr);
    }

    public Set<String> getUserLikedSongs() { return userLikedSongs; }
}
