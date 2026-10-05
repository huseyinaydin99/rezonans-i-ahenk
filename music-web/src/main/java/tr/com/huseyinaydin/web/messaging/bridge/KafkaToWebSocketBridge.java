package tr.com.huseyinaydin.web.messaging.bridge;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.infrastructure.messaging.kafka.config.KafkaTopicConfig;

/**
 * Arka plandaki (Kafka) asenkron olayları alıp, anlık (Real-Time) olarak
 * tarayıcıdaki son kullanıcılara (WebSocket) fırlatan köprü sınıfı.
 */
@Component
public class KafkaToWebSocketBridge {

    private final SimpMessagingTemplate messagingTemplate;

    public KafkaToWebSocketBridge(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = KafkaTopicConfig.MUSIC_EVENTS_TOPIC, groupId = "websocket-broadcast-group")
    public void broadcastToClients(Object event) {
        System.out.println("[KÖPRÜ] Kafka olayı WebSocket üzerinden canlı olarak tarayıcılara iletiliyor...");
        
        // Örn: Tarayıcılara yeni yayınlanan şarkının bildirimini canlı gönder
        String notificationMessage = "Canlı Akış: Yeni bir sistem olayı tetiklendi! " + event.toString();
        
        messagingTemplate.convertAndSend("/topic/notifications", notificationMessage);
    }
}
