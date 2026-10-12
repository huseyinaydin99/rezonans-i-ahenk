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
    private final tr.com.huseyinaydin.domain.repositories.ISongRepository songRepository;
    private final tr.com.huseyinaydin.domain.repositories.IArtistRepository artistRepository;

    public ArtistServiceImpl(UploadPipelineService uploadPipelineService,
                             IStorageService storageService,
                             @org.springframework.beans.factory.annotation.Autowired(required = false) tr.com.huseyinaydin.domain.repositories.ISongRepository songRepository,
                             @org.springframework.beans.factory.annotation.Autowired(required = false) tr.com.huseyinaydin.domain.repositories.IArtistRepository artistRepository) {
        this.uploadPipelineService = uploadPipelineService;
        this.storageService = storageService;
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    @Override
    public ArtistDashboardDto getDashboardStats(String artistId) {
        return new ArtistDashboardDto(artistId, "The Weeknd", 1450000, 89000000L, 42);
    }

    @Override
    @Transactional
    public void publishNewSong(String artistId, String title, InputStream audioStream, String fileName, long fileSize, String contentType) {
        String minioUrl = storageService.uploadAudioFile(fileName, audioStream, fileSize, contentType);
        byte[] mockData = new byte[(int) Math.min(fileSize > 0 ? fileSize : 1024, 1024)]; 
        String format = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "mp3";
        SongUploadRequest request = new SongUploadRequest(fileName, format, mockData, artistId);
        uploadPipelineService.processUpload(request);

        if (songRepository != null) {
            UUID artistUid;
            try {
                artistUid = UUID.fromString(artistId);
            } catch (Exception e) {
                artistUid = UUID.randomUUID();
            }
            tr.com.huseyinaydin.domain.song.Song song = tr.com.huseyinaydin.domain.song.Song.builder()
                    .title(title != null && !title.isBlank() ? title : fileName)
                    .coverImageUrl("https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300")
                    .audioUrl(minioUrl != null ? minioUrl : "/uploads/" + fileName)
                    .duration(java.time.Duration.ofSeconds(180))
                    .tier(tr.com.huseyinaydin.domain.enums.MembershipTier.BASIC)
                    .releaseDate(LocalDateTime.now())
                    .artistId(artistUid)
                    .genreId(UUID.randomUUID())
                    .build();
            songRepository.save(song);
        }
    }

    // --- YENİ EKLENEN VİTRİN METOTLARI ---
    @Override
    public List<ResultArtistDto> getFeaturedArtists() {
        return Arrays.asList(
            new ResultArtistDto("art-1", "The Midnight & Nikki Flores", "/images/artists/midnight-cockpit.svg", 1893317L, true),
            new ResultArtistDto("art-2", "The Midnight", "/images/artists/midnight-cockpit.svg", 1002914L, true),
            new ResultArtistDto("art-3", "Royalty Free Music Background", "/images/artists/neon-square.svg", 120823L, true),
            new ResultArtistDto("art-4", "Zee-ker", "/images/artists/midnight-sax.svg", 310813L, true),
            new ResultArtistDto("art-5", "Lachi James", "/images/artists/synthwave-triangle.svg", 760472L, true),
            new ResultArtistDto("art-6", "Serene Serene", "/images/artists/cyber-portrait.svg", 552636L, true),
            new ResultArtistDto("art-7", "Kavinsky", "/images/artists/kavinsky.svg", 199068L, true),
            new ResultArtistDto("art-8", "Kavinsky, Angèle & Phoenix", "/images/artists/nightcall-group.svg", 464858L, true),
            new ResultArtistDto("art-9", "FM-84 & Ollie Wride", "/images/artists/retro-sun.svg", 983997L, true),
            new ResultArtistDto("art-10", "FM-84", "/images/artists/retro-sun.svg", 1617667L, true),
            new ResultArtistDto("art-11", "FM-84, Timecop1983 & Josh Dally", "/images/artists/retro-sun.svg", 729879L, true),
            new ResultArtistDto("art-12", "Lazerhawk", "/images/artists/lazerhawk-car.svg", 1535130L, true)
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
