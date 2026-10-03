package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.like.dtos.LikeToggleDto;
import java.util.List;
import java.util.UUID;

public interface ILikeService {
    boolean toggleLike(LikeToggleDto dto);
    List<String> getLikedSongs(UUID userId); // Simülasyon için String listesi dönüyor
}
