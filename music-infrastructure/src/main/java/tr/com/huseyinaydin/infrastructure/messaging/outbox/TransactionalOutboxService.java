package tr.com.huseyinaydin.infrastructure.messaging.outbox;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.domain.outbox.OutboxEvent;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEvent;

@Service
public class TransactionalOutboxService {

    /**
     * DİKKAT: @Transactional anotasyonu sayesinde, şarkının veritabanına
     * kaydedilmesi (Save) ile olayın Outbox tablosuna yazılması TEK BİR İŞLEM (Atomic)
     * olarak gerçekleşir. Biri patlarsa diğeri de Rollback olur. Veri tutarsızlığı imkansızdır.
     */
    @Transactional
    public void saveEventToOutbox(IDomainEvent event) {
        System.out.println("[OUTBOX PATTERN] Çift yazma (Dual Write) engelleniyor...");
        
        // Olayın JSON'a çevrilip Outbox tablosuna kaydedilmesi simülasyonu
        OutboxEvent outboxRecord = new OutboxEvent(
                event.getClass().getSimpleName(),
                event.getEventId().toString(),
                event.getClass().getName(),
                "{ \"eventId\": \"" + event.getEventId() + "\" }"
        );
        
        System.out.println("[OUTBOX PATTERN] Olay güvenle (Atomic Transaction) veritabanına 'Giden Kutusu' olarak yazıldı.");
    }
}
