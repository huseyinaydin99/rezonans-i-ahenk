package tr.com.huseyinaydin.application.song.upload;

import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

public class VirusScanHandler extends SongUploadHandler {
    @Override
    public void handle(SongUploadRequest request) {
        System.out.println("[ZİNCİR 2] Virüs ve Kötü Amaçlı Yazılım (Malware) taraması yapılıyor...");
        
        // Simüle edilmiş güvenlik taraması
        if (request.getFileData() == null || request.getFileData().length == 0) {
            throw new BusinessException("Dosya bozuk veya zararlı yazılım tespit edildi!");
        }
        
        System.out.println("-> Tarama temiz.");
        super.handle(request); // Başarılıysa bir sonraki halkaya geç
    }
}
