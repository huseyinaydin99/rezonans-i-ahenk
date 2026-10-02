package tr.com.huseyinaydin.infrastructure.search.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring Data Elasticsearch yapılandırması (Simülasyon).
 * application.properties üzerinden uri okunarak bağlantı sağlanır.
 */
@Configuration
public class ElasticsearchConfig {
    // Elasticsearch Client bean'leri burada tanımlanır.
    public static final String SONG_INDEX = "songs_index";
}
