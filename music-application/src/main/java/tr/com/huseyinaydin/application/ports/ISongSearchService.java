package tr.com.huseyinaydin.application.ports;

import java.util.List;
import java.util.Map;

public interface ISongSearchService {
    List<Map<String, Object>> searchSongs(String keyword);
    Map<String, Object> getSongById(Long songId);
    List<Map<String, Object>> getSongsByIds(List<Long> songIds);
    Map<String, Integer> getGenreSongCounts();
    default void registerSong(Long id, String title, String artist, String genre, String tier, String coverUrl, String duration) {}
}
