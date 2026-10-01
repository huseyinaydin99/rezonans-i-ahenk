package tr.com.huseyinaydin.application.events.handlers;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.events.models.MembershipUpgradedEvent;

@Component
public class SendWelcomeEmailOnMembershipUpgrade {

    @Async
    @EventListener
    public void handle(MembershipUpgradedEvent event) {
        // Bu işlem asenkron çalışır, ana thread'i bloklamaz
        System.out.println("[ASYNC EVENT HANDLER] Mail Servisi Tetiklendi!");
        System.out.println("-> Kullanıcı: " + event.getUserId() + " | Yeni Paket: " + event.getNewTier());
        // Simüle edilmiş mail gönderme işlemi...
    }
}
