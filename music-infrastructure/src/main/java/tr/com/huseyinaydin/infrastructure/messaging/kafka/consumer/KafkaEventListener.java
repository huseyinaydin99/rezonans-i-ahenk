package tr.com.huseyinaydin.infrastructure.messaging.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.infrastructure.messaging.kafka.config.KafkaTopicConfig;

@Component
public class KafkaEventListener {

    @KafkaListener(topics = KafkaTopicConfig.MUSIC_EVENTS_TOPIC, groupId = "ml-recommendation-group")
    public void listenForMLUpdate(Object event) {
        System.out.println("[KAFKA CONSUMER - ML GRUBU] Olay kuyruktan okundu ve Yapay Zeka motoru besleniyor... Veri: " + event.toString());
        // ML algoritması beslenir
    }

    @KafkaListener(topics = KafkaTopicConfig.MUSIC_EVENTS_TOPIC, groupId = "history-tracking-group")
    public void listenForHistory(Object event) {
        System.out.println("[KAFKA CONSUMER - GEÇMİŞ GRUBU] Olay kuyruktan okundu ve Kullanıcı Dinleme Geçmişi güncelleniyor...");
        // Veritabanına geçmiş loglanır
    }
}
