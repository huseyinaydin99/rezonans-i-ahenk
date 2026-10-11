package tr.com.huseyinaydin.application.events.handlers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.events.models.MembershipUpgradedEvent;

@Component
public class SendWelcomeEmailOnMembershipUpgrade {
    private static final Logger log = LoggerFactory.getLogger(SendWelcomeEmailOnMembershipUpgrade.class);


    @Async
    @EventListener
    public void handle(MembershipUpgradedEvent event) {
        // Bu işlem asenkron çalışır, ana thread'i bloklamaz
        log.info("[ASYNC EVENT HANDLER] Mail Servisi Tetiklendi!");
        log.info("-> Kullanıcı: " + event.getUserId() + " | Yeni Paket: " + event.getNewTier());
        // Simüle edilmiş mail gönderme işlemi...
    }
}
