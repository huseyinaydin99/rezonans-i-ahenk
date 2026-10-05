package tr.com.huseyinaydin.application.song.upload;

import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

public class CopyrightCheckHandler extends SongUploadHandler {
    @Override
    public void handle(SongUploadRequest request) {
        System.out.println("[ZİNCİR 3] Telif Hakkı (Copyright / Content ID) veri tabanında eşleşme aranıyor...");
        
        // Simüle edilmiş telif hakkı taraması
        if (request.getFileName().toLowerCase().contains("copyrighted")) {
            throw new BusinessException("ERR", "Telif hakkı ihlali tespit edildi! Bu şarkıyı yükleyemezsiniz.");
        }
        
        System.out.println("-> Telif ihlali bulunmadı. İşlem başarılı.");
        super.handle(request); // Başarılıysa bir sonraki halkaya geç (veya zincir biter)
    }
}
