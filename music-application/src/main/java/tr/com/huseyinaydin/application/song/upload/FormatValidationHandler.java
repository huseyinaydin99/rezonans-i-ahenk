package tr.com.huseyinaydin.application.song.upload;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

public class FormatValidationHandler extends SongUploadHandler {
    private static final Logger log = LoggerFactory.getLogger(FormatValidationHandler.class);

    @Override
    public void handle(SongUploadRequest request) {
        log.info("[ZİNCİR 1] Format Kontrolü yapılıyor...");
        
        if (!"mp3".equalsIgnoreCase(request.getFormat()) && !"wav".equalsIgnoreCase(request.getFormat())) {
            throw new BusinessException("ERR", "Geçersiz dosya formatı! Sadece MP3 ve WAV desteklenir.");
        }
        
        log.info("-> Format geçerli.");
        super.handle(request); // Başarılıysa bir sonraki halkaya geç
    }
}
