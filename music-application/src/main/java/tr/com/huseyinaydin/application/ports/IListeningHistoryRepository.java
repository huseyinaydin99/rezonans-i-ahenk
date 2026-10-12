package tr.com.huseyinaydin.application.ports;

import java.util.List;
import java.util.Map;

/**
 * Dinleme geçmişi ve dinlenme istatistikleri (PlayCount) için port arayüzü.
 */
public interface IListeningHistoryRepository {
    long incrementPlayCount(Long songId);
    long getPlayCount(Long songId);
    void recordListeningHistory(String userEmail, Long songId);
    List<Map<String, Object>> getRecentHistory(String userEmail, int limit);
}
