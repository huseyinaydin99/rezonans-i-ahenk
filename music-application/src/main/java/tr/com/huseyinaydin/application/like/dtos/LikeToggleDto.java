package tr.com.huseyinaydin.application.like.dtos;

import java.util.UUID;

public class LikeToggleDto {
    private UUID userId;
    private UUID songId;

    public LikeToggleDto(UUID userId, UUID songId) {
        this.userId = userId;
        this.songId = songId;
    }

    public UUID getUserId() { return userId; }
    public UUID getSongId() { return songId; }
}
