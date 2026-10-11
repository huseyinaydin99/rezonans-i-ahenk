package tr.com.huseyinaydin.application.song.upload;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UploadPipelineService {
    private static final Logger log = LoggerFactory.getLogger(UploadPipelineService.class);


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

        log.info("==== Şarkı Yükleme Süreci Başlıyor ====");
        // İsteğin zincire (ilk halkadan) fırlatılması
        formatHandler.handle(request);
        log.info("==== Şarkı Başarıyla Sisteme Yüklendi ====");
    }
}
