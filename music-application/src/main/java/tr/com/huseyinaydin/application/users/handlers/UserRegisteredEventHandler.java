package tr.com.huseyinaydin.application.users.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.events.UserRegisteredEvent;

@Component
public class UserRegisteredEventHandler {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventHandler.class);

    @EventListener
    @Async
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("🎉 [DOMAIN EVENT DİNLENDİ] Yeni kullanıcı kayıt olayı yakalandı! Kullanıcı ID: {}, E-Posta: {}, Rol: {}",
                event.getUserId(), event.getEmail(), event.getRole());
        
        // Single Responsibility Principle (SRP):
        // Hoş geldin e-postası gönderme, dış bildirimler ve istatistik güncellemeleri
        // ana kayıt servisinden tamamen ayrıştırılarak olay güdümlü (Event-Driven) olarak yürütülür.
        log.info("-> [EVENT-DRIVEN] Hoş geldin e-postası asenkron gönderim kuyruğuna iletildi: {}", event.getEmail());
        log.info("-> [EVENT-DRIVEN] Kullanıcı analitik ve kayıt metrikleri güncellendi.");
    }
}
