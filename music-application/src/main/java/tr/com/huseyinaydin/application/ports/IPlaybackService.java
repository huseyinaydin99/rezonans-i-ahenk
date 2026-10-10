package tr.com.huseyinaydin.application.ports;

public interface IPlaybackService {
    void recordListenEvent(String userId, Long songId);
    String getAudioUrl(Long songId);
    boolean hasAccess(Long songId, String userTier);
    Integer getRequiredTierLevel(Long songId);
}
