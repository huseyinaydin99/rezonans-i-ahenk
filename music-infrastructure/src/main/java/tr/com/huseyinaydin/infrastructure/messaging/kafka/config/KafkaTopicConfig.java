package tr.com.huseyinaydin.infrastructure.messaging.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String MUSIC_EVENTS_TOPIC = "rezonans-music-events";

    @Bean
    public NewTopic musicEventsTopic() {
        return TopicBuilder.name(MUSIC_EVENTS_TOPIC)
                .partitions(3) // Yüksek trafik için bölümlendirme
                .replicas(1)
                .build();
    }
}
