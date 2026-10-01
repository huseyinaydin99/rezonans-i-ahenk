package tr.com.huseyinaydin.application.events.handlers;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.events.models.SongPlayedEvent;

@Component
public class UpdateMLModelOnSongPlayed {

    @Async
    @EventListener
    public void handle(SongPlayedEvent event) {
        // Makine öğrenmesi modeli asenkron güncellenir
        System.out.println("[ASYNC EVENT HANDLER] ML(Yapay Zeka) Tavsiye Motoru Güncelleniyor...");
        System.out.println("-> Dinlenen Şarkı: " + event.getSongId() + " | Kullanıcı: " + event.getUserId());
        // ML modelini eğitme kodları...
    }
}
