package tr.com.huseyinaydin.infrastructure.nosql.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring Data Cassandra yapılandırması (Simülasyon).
 * application.properties üzerinden keyspace ve contact-points okunarak bağlantı sağlanır.
 */
public class CassandraConfig {
    public static final String KEYSPACE = "music_keyspace";
}
