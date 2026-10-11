package tr.com.huseyinaydin.infrastructure.adapters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(SongSearchAdapter.class);


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
                Object idVal = es.getId();
                try {
                    idVal = Long.parseLong(es.getId());
                } catch (NumberFormatException ignored) {}
                map.put("id", idVal);
                map.put("title", es.getTitle());
                map.put("artist", es.getArtist());
                map.put("cover", es.getCoverUrl());
                map.put("tier", es.getTier());
                filtered.add(map);
            }
        } catch (Exception e) {
            // Hata durumunda (örneğin Elasticsearch kapalıysa) sessizce boş döner veya fallback yapılabilir.
            log.error("Elasticsearch arama hatası: " + e.getMessage());
        }
        return filtered;
    }
}
