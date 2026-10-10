package tr.com.huseyinaydin.infrastructure.adapters;

import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.infrastructure.IElasticSongRepository;
import tr.com.huseyinaydin.infrastructure.ElasticSong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SongSearchAdapter implements ISongSearchService {

    private final IElasticSongRepository elasticSongRepository;

    public SongSearchAdapter(IElasticSongRepository elasticSongRepository) {
        this.elasticSongRepository = elasticSongRepository;
    }

    @Override
    public List<Map<String, Object>> searchSongs(String keyword) {
        List<Map<String, Object>> filtered = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return filtered;
        }

        try {
            // Elasticsearch üzerinden "Like/Containing" araması
            List<ElasticSong> results = elasticSongRepository.findByTitleContainingOrArtistContaining(keyword, keyword);
            for (ElasticSong es : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", Long.parseLong(es.getId()));
                map.put("title", es.getTitle());
                map.put("artist", es.getArtist());
                map.put("cover", es.getCoverUrl());
                map.put("tier", es.getTier());
                filtered.add(map);
            }
        } catch (Exception e) {
            // Hata durumunda (örneğin Elasticsearch kapalıysa) sessizce boş döner veya fallback yapılabilir.
            System.err.println("Elasticsearch arama hatası: " + e.getMessage());
        }
        return filtered;
    }
}
