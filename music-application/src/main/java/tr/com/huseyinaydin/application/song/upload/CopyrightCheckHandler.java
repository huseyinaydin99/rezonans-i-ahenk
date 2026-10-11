package tr.com.huseyinaydin.application.song.upload;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

public class CopyrightCheckHandler extends SongUploadHandler {
    private static final Logger log = LoggerFactory.getLogger(CopyrightCheckHandler.class);

    @Override
    public void handle(SongUploadRequest request) {
        log.info("[ZİNCİR 3] Telif Hakkı (Copyright / Content ID) veri tabanında eşleşme aranıyor...");
        
        // Simüle edilmiş telif hakkı taraması
        if (request.getFileName().toLowerCase().contains("copyrighted")) {
            throw new BusinessException("ERR", "Telif hakkı ihlali tespit edildi! Bu şarkıyı yükleyemezsiniz.");
        }
        
        log.info("-> Telif ihlali bulunmadı. İşlem başarılı.");
        super.handle(request); // Başarılıysa bir sonraki halkaya geç (veya zincir biter)
    }
}
