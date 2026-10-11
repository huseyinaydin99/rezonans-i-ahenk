package tr.com.huseyinaydin.application.song.upload;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

public class VirusScanHandler extends SongUploadHandler {
    private static final Logger log = LoggerFactory.getLogger(VirusScanHandler.class);

    @Override
    public void handle(SongUploadRequest request) {
        log.info("[ZİNCİR 2] Virüs ve Kötü Amaçlı Yazılım (Malware) taraması yapılıyor...");
        
        // Simüle edilmiş güvenlik taraması
        if (request.getFileData() == null || request.getFileData().length == 0) {
            throw new BusinessException("ERR", "Dosya bozuk veya zararlı yazılım tespit edildi!");
        }
        
        log.info("-> Tarama temiz.");
        super.handle(request); // Başarılıysa bir sonraki halkaya geç
    }
}
