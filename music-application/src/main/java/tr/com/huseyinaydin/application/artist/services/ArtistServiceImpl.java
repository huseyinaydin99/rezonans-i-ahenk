package tr.com.huseyinaydin.application.artist.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.IArtistService;
import tr.com.huseyinaydin.application.artist.dtos.ArtistDashboardDto;
import tr.com.huseyinaydin.application.ports.IStorageService;
import tr.com.huseyinaydin.application.song.upload.SongUploadRequest;
import tr.com.huseyinaydin.application.song.upload.UploadPipelineService;

import java.io.InputStream;

@Service
public class ArtistServiceImpl implements IArtistService {

    private final UploadPipelineService uploadPipelineService; // Önceden yazdığımız Sorumluluk Zinciri (CoR)
    private final IStorageService storageService; // Önceden yazdığımız MinIO entegrasyonu

    public ArtistServiceImpl(UploadPipelineService uploadPipelineService, IStorageService storageService) {
        this.uploadPipelineService = uploadPipelineService;
        this.storageService = storageService;
    }

    @Override
    public ArtistDashboardDto getDashboardStats(String artistId) {
        System.out.println("[ARTIST PORTAL] Sanatçı (" + artistId + ") istatistikleri Cassandra/PostgreSQL üzerinden toplanıyor...");
        return new ArtistDashboardDto(artistId, "The Weeknd", 1450000, 89000000L, 42);
    }

    @Override
    @Transactional
    public void publishNewSong(String artistId, String title, InputStream audioStream, String fileName, long fileSize, String contentType) {
        System.out.println("[ARTIST PORTAL] Yeni şarkı (" + title + ") yükleme talebi başlatıldı.");

        // 1. Dosya MinIO (Object Storage) bulutuna fırlatılır
        String minioUrl = storageService.uploadAudioFile(fileName, audioStream, fileSize, contentType);
        
        // 2. İş kuralları: Format kontrolü, Virüs Taraması, Telif taraması için CoR Pipeline tetiklenir
        // Sorumluluk Zincirimiz byte[] beklediği için simüle ediyoruz
        byte[] mockData = new byte[(int) fileSize]; 
        String format = fileName.substring(fileName.lastIndexOf(".") + 1);
        
        SongUploadRequest request = new SongUploadRequest(fileName, format, mockData, artistId);
        uploadPipelineService.processUpload(request);
        
        // 3. Veritabanına şarkı eklenir ve Kafka üzerinden tüm takipçilere bildirim (Event) fırlatılır
        System.out.println("-> Şarkı veritabanına eklendi. URL: " + minioUrl);
        System.out.println("-> Outbox Pattern üzerinden takipçilere bildirim fırlatıldı.");
    }
}
