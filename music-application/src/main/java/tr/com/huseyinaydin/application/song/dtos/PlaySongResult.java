package tr.com.huseyinaydin.application.song.dtos;

/**
 * Sunucu taraflı şarkı çalma yetki ve oynatma yanıt DTO'su.
 */
public record PlaySongResult(
        boolean authorized,
        String requiredTier,
        String message,
        Long songId,
        String title,
        String artist,
        String audioUrl,
        String cover,
        long playCount
) {
    public static PlaySongResult unauthorized(String requiredTier, String message) {
        return new PlaySongResult(false, requiredTier, message, null, null, null, null, null, 0);
    }

    public static PlaySongResult authorized(Long songId, String title, String artist, String audioUrl, String cover, long playCount) {
        return new PlaySongResult(true, null, "Başarılı", songId, title, artist, audioUrl, cover, playCount);
    }
}
