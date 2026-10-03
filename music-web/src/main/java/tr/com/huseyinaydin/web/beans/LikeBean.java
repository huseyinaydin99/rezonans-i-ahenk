package tr.com.huseyinaydin.web.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import tr.com.huseyinaydin.application.ports.ILikeService;
import tr.com.huseyinaydin.application.like.dtos.LikeToggleDto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Named
@SessionScoped
public class LikeBean implements Serializable {

    @Inject
    private ILikeService likeService;

    // Arayüzde beğeni butonunun o anki durumu (Kırmızı Kalp / Boş Kalp)
    private boolean isLiked = false;
    private List<String> userLikedSongs;

    public void toggleFavorite(String songIdStr) {
        UUID userId = UUID.randomUUID(); // Gerçekte SecurityContext'ten alınır
        UUID songId = UUID.fromString(songIdStr);
        
        LikeToggleDto dto = new LikeToggleDto(userId, songId);
        
        // Servis bize şarkının yeni durumunu (Beğenildi mi / Geri mi alındı) döner
        this.isLiked = likeService.toggleLike(dto);
        
        System.out.println("[JSF AJAX] Favori butonuna basıldı. Yeni Durum: " + (this.isLiked ? "EKLENDİ" : "ÇIKARILDI"));
    }

    public void loadLikedSongs() {
        UUID userId = UUID.randomUUID();
        this.userLikedSongs = likeService.getLikedSongs(userId);
    }

    public boolean getIsLiked() { return isLiked; }
    public List<String> getUserLikedSongs() { return userLikedSongs; }
}
