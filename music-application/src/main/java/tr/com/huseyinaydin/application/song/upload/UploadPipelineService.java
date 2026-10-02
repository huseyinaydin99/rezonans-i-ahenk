package tr.com.huseyinaydin.application.song.upload;

import org.springframework.stereotype.Service;

@Service
public class UploadPipelineService {

    /**
     * Şarkı yükleme isteğini Sorumluluk Zinciri (Chain of Responsibility) hattından geçirir.
     */
    public void processUpload(SongUploadRequest request) {
        
        // Zincirin (Chain) oluşturulması
        SongUploadHandler formatHandler = new FormatValidationHandler();
        SongUploadHandler virusHandler = new VirusScanHandler();
        SongUploadHandler copyrightHandler = new CopyrightCheckHandler();

        // Halkaların birbirine bağlanması (Fluent Design)
        formatHandler.setNext(virusHandler).setNext(copyrightHandler);

        System.out.println("==== Şarkı Yükleme Süreci Başlıyor ====");
        // İsteğin zincire (ilk halkadan) fırlatılması
        formatHandler.handle(request);
        System.out.println("==== Şarkı Başarıyla Sisteme Yüklendi ====");
    }
}
