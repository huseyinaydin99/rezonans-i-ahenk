package tr.com.huseyinaydin.application.song.upload;

import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

public class FormatValidationHandler extends SongUploadHandler {
    @Override
    public void handle(SongUploadRequest request) {
        System.out.println("[ZİNCİR 1] Format Kontrolü yapılıyor...");
        
        if (!"mp3".equalsIgnoreCase(request.getFormat()) && !"wav".equalsIgnoreCase(request.getFormat())) {
            throw new BusinessException("ERR", "Geçersiz dosya formatı! Sadece MP3 ve WAV desteklenir.");
        }
        
        System.out.println("-> Format geçerli.");
        super.handle(request); // Başarılıysa bir sonraki halkaya geç
    }
}
