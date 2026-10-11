package tr.com.huseyinaydin.application.events.handlers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.events.models.SongPlayedEvent;

@Component
public class UpdateMLModelOnSongPlayed {
    private static final Logger log = LoggerFactory.getLogger(UpdateMLModelOnSongPlayed.class);


    @Async
    @EventListener
    public void handle(SongPlayedEvent event) {
        // Makine öğrenmesi modeli asenkron güncellenir
        log.info("[ASYNC EVENT HANDLER] ML(Yapay Zeka) Tavsiye Motoru Güncelleniyor...");
        log.info("-> Dinlenen Şarkı: " + event.getSongId() + " | Kullanıcı: " + event.getUserId());
        // ML modelini eğitme kodları...
    }
}
