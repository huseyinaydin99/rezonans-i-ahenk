package tr.com.huseyinaydin.application.artist.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.IArtistService;
import tr.com.huseyinaydin.application.artist.dtos.ArtistDashboardDto;
import tr.com.huseyinaydin.application.artist.dtos.ResultArtistDto;
import tr.com.huseyinaydin.application.artist.dtos.ArtistDetailDto;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.application.ports.IStorageService;
import tr.com.huseyinaydin.application.song.upload.SongUploadRequest;
import tr.com.huseyinaydin.application.song.upload.UploadPipelineService;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class ArtistServiceImpl implements IArtistService {

    private final UploadPipelineService uploadPipelineService;
    private final IStorageService storageService;

    public ArtistServiceImpl(UploadPipelineService uploadPipelineService, IStorageService storageService) {
        this.uploadPipelineService = uploadPipelineService;
        this.storageService = storageService;
    }

    @Override
    public ArtistDashboardDto getDashboardStats(String artistId) {
        return new ArtistDashboardDto(artistId, "The Weeknd", 1450000, 89000000L, 42);
    }

    @Override
    @Transactional
    public void publishNewSong(String artistId, String title, InputStream audioStream, String fileName, long fileSize, String contentType) {
        String minioUrl = storageService.uploadAudioFile(fileName, audioStream, fileSize, contentType);
        byte[] mockData = new byte[(int) fileSize]; 
        String format = fileName.substring(fileName.lastIndexOf(".") + 1);
        SongUploadRequest request = new SongUploadRequest(fileName, format, mockData, artistId);
        uploadPipelineService.processUpload(request);
    }

    // --- YENİ EKLENEN VİTRİN METOTLARI ---
    @Override
    public List<ResultArtistDto> getFeaturedArtists() {
        // Gerçekte veritabanı veya Elasticsearch'ten en çok dinlenen sanatçılar çekilir
        return Arrays.asList(
            new ResultArtistDto("A1", "The Weeknd", "https://i.scdn.co/image/ab6761610000e5eb2a433291244bb49afaf82a5c", 112000000, true),
            new ResultArtistDto("A2", "Taylor Swift", "https://i.scdn.co/image/ab6761610000e5eb5a00969a4698c3132a15fbb0", 98000000, true),
            new ResultArtistDto("A3", "Daft Punk", "https://i.scdn.co/image/ab6761610000e5ebc3b0eb6014e7a7605dcc1b50", 45000000, false),
            new ResultArtistDto("A4", "Hans Zimmer", "https://i.scdn.co/image/ab6761610000e5eb00921092161ce3dc16a3fcfa", 12000000, true),
            new ResultArtistDto("A5", "Adele", "https://i.scdn.co/image/ab6761610000e5eb68f6e5892075d7f22615bd17", 65000000, true)
        );
    }

    @Override
    public ArtistDetailDto getArtistDetail(String artistId) {
        // Mock veri; gerçekte ID'ye göre DB'den çekilecek
        List<SongResponse> popularSongs = Arrays.asList(
            new SongResponse(UUID.randomUUID(), "Starboy", "https://i.scdn.co/image/ab67616d00001e024718e2b124f79258be7bc452", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", 230, 1500000, "ELITE", LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID()),
            new SongResponse(UUID.randomUUID(), "Blinding Lights", "https://i.scdn.co/image/ab67616d00001e028863bc11d2aaf12ac1715a40", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", 200, 3200000, "PREMIUM", LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID())
        );

        return new ArtistDetailDto(
            artistId, 
            "The Weeknd", 
            "https://i.scdn.co/image/ab6761610000e5eb2a433291244bb49afaf82a5c", 
            "Kanadalı şarkıcı, söz yazarı ve plak yapımcısı. Karanlık R&B tarzı ve sinematik pop melodileriyle dünyayı kasıp kavuran ikonik bir müzisyen.", 
            112000000, 
            true, 
            popularSongs
        );
    }
}
