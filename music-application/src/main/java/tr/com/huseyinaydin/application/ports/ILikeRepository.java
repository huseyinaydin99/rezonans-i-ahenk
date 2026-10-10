package tr.com.huseyinaydin.application.ports;

import java.util.List;

public interface ILikeRepository {
    void addLike(String userEmail, Long songId);
    void removeLike(String userEmail, Long songId);
    boolean isLiked(String userEmail, Long songId);
    List<Long> getLikedSongIds(String userEmail);
}
