package tr.com.huseyinaydin.infrastructure.messaging.kafka.consumer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.infrastructure.messaging.kafka.config.KafkaTopicConfig;

@Component
public class KafkaEventListener {
    private static final Logger log = LoggerFactory.getLogger(KafkaEventListener.class);


    @KafkaListener(topics = KafkaTopicConfig.MUSIC_EVENTS_TOPIC, groupId = "ml-recommendation-group")
    public void listenForMLUpdate(Object event) {
        log.info("[KAFKA CONSUMER - ML GRUBU] Olay kuyruktan okundu ve Yapay Zeka motoru besleniyor... Veri: " + event.toString());
        // ML algoritması beslenir
    }

    @KafkaListener(topics = KafkaTopicConfig.MUSIC_EVENTS_TOPIC, groupId = "history-tracking-group")
    public void listenForHistory(Object event) {
        log.info("[KAFKA CONSUMER - GEÇMİŞ GRUBU] Olay kuyruktan okundu ve Kullanıcı Dinleme Geçmişi güncelleniyor...");
        // Veritabanına geçmiş loglanır
    }
}
