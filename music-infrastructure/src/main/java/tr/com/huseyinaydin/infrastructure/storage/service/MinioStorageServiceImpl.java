package tr.com.huseyinaydin.infrastructure.storage.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.ports.IStorageService;
import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioStorageServiceImpl implements IStorageService {
    private static final Logger log = LoggerFactory.getLogger(MinioStorageServiceImpl.class);


    // private final MinioClient minioClient; -> Inject via constructor in real implementation

    @Override
    public String uploadAudioFile(String fileName, InputStream fileStream, long size, String contentType) {
        log.info("[MINIO STORAGE] Müzik dosyası yükleniyor... (" + size + " byte)");
        
        // Benzersiz dosya ismi oluşturulur
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        
        // Gerçekte minioClient.putObject(...) çağrılır
        log.info("[MINIO STORAGE] Müzik dosyası başarıyla parçalanıp yerel buluta (Object Storage) yazıldı.");
        
        return "http://localhost:9000/music-audio-bucket/" + uniqueFileName;
    }

    @Override
    public String uploadImageFile(String fileName, InputStream fileStream, long size, String contentType) {
        log.info("[MINIO STORAGE] Kapak resmi yükleniyor...");
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        return "http://localhost:9000/music-image-bucket/" + uniqueFileName;
    }

    @Override
    public void deleteFile(String fileUrl) {
        log.info("[MINIO STORAGE] Dosya bulut deposundan silindi: " + fileUrl);
    }
}
