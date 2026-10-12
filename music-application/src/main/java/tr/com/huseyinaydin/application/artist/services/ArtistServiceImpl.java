package tr.com.huseyinaydin.application.artist.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.IArtistService;
import tr.com.huseyinaydin.application.artist.dtos.ArtistDashboardDto;
import tr.com.huseyinaydin.application.artist.dtos.ResultArtistDto;
import tr.com.huseyinaydin.application.artist.dtos.ArtistDetailDto;
import tr.com.huseyinaydin.application.artist.dtos.ArtistSongDto;
import tr.com.huseyinaydin.application.ports.IStorageService;
import tr.com.huseyinaydin.application.song.upload.SongUploadRequest;
import tr.com.huseyinaydin.application.song.upload.UploadPipelineService;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ArtistServiceImpl implements IArtistService {

    private static final Logger log = LoggerFactory.getLogger(ArtistServiceImpl.class);

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
        return new ArtistDashboardDto(artistId, "The Midnight", 1450000, 89000000L, 42);
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
        if (artistId == null || artistId.isBlank()) {
            return null;
        }

        String normalizedId = artistId.trim();

        // 1. Veritabanından UUID ile arama (Entity mevcutsa)
        if (artistRepository != null) {
            try {
                UUID uid = UUID.fromString(normalizedId);
                var artistOpt = artistRepository.findById(uid);
                if (artistOpt.isPresent()) {
                    var entity = artistOpt.get();
                    return new ArtistDetailDto(
                        entity.getId().toString(),
                        entity.getName(),
                        entity.getImageUrl() != null ? entity.getImageUrl() : "/images/artists/midnight-cockpit.svg",
                        entity.getBio() != null ? entity.getBio() : "Rezonans-ı Ahenk platformunun yetenekli sanatçısı.",
                        entity.getMonthlyListeners() > 0 ? entity.getMonthlyListeners() : 1250000L,
                        entity.isVerified(),
                        findSongsForArtist(entity.getName())
                    );
                }
            } catch (IllegalArgumentException ignored) {
                // UUID formatında değilse statik ve dinamik katalog eşleştirmesine devam et
            }
        }

        // 2. Platform Sanatçı Kataloğu (Öne Çıkan Synthwave / Retro Sanatçıları)
        Map<String, ArtistDetailDto> catalog = buildArtistCatalog();

        // Doğrudan ID eşleşmesi (art-1, art-2...)
        String lookupKey = normalizedId.toLowerCase(Locale.ROOT);
        if (catalog.containsKey(lookupKey)) {
            return catalog.get(lookupKey);
        }

        // İsim veya slug ile eşleştirme
        for (ArtistDetailDto dto : catalog.values()) {
            if (dto.getId().equalsIgnoreCase(normalizedId) ||
                dto.getName().equalsIgnoreCase(normalizedId) ||
                dto.getName().toLowerCase(Locale.ROOT).contains(lookupKey)) {
                return dto;
            }
        }

        // Diğer bilinen sanatçılar (Depeche Mode, Dua Lipa, The Weeknd)
        if (lookupKey.contains("depeche")) {
            return createDepecheModeDetail(normalizedId);
        }
        if (lookupKey.contains("dua") || lookupKey.contains("lipa")) {
            return createDuaLipaDetail(normalizedId);
        }
        if (lookupKey.contains("weeknd")) {
            return createTheWeekndDetail(normalizedId);
        }

        // Bulunamadıysa genel retro sanatçı şablonu (Fallback)
        return new ArtistDetailDto(
            normalizedId,
            capitalize(normalizedId.replace("-", " ")),
            "/images/artists/midnight-cockpit.svg",
            "Analog synthesizer tınıları ve sinematik retro frekanslarla Rezonans-ı Ahenk vitrininde yer alan seçkin sanatçı.",
            1050000L,
            true,
            findSongsForArtist(normalizedId)
        );
    }

    private Map<String, ArtistDetailDto> buildArtistCatalog() {
        Map<String, ArtistDetailDto> map = new LinkedHashMap<>();

        // art-1: The Midnight & Nikki Flores
        map.put("art-1", new ArtistDetailDto(
            "art-1",
            "The Midnight & Nikki Flores",
            "/images/artists/midnight-cockpit.svg",
            "Los Angeles merkezli synthwave ikilisi The Midnight ve Nikki Flores'in büyüleyici ortak projesi. 80'lerin nostaljik synth dokuları ve duygusal vokaller.",
            1893317L,
            true,
            List.of(
                new ArtistSongDto(11L, "Light Years", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "3:32", 1893317L, "Premium", 3, "The Midnight & Nikki Flores", "Synthwave"),
                new ArtistSongDto(34L, "Jason", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "5:31", 1240000L, "Gold", 2, "The Midnight & Nikki Flores", "Synthwave")
            )
        ));

        // art-2: The Midnight
        map.put("art-2", new ArtistDetailDto(
            "art-2",
            "The Midnight",
            "/images/artists/midnight-cockpit.svg",
            "Synthwave müziğin zirve temsilcilerinden The Midnight; sinematik saksafon soloları, analog synthesizer melodileri ve retro-fütüristik atmosferiyle zamansız bir deneyim sunuyor.",
            1002914L,
            true,
            List.of(
                new ArtistSongDto(30L, "Sunset", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", "5:26", 2310000L, "Gold", 2, "The Midnight", "Synthwave"),
                new ArtistSongDto(31L, "Days of Thunder", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3", "5:24", 1980000L, "Basic", 1, "The Midnight", "Synthwave"),
                new ArtistSongDto(12L, "River of Darkness (feat. Timecop1983)", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3", "6:05", 1750000L, "Premium", 3, "The Midnight", "Synthwave"),
                new ArtistSongDto(32L, "Gloria", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3", "6:02", 1430000L, "Elit", 4, "The Midnight", "Synthwave"),
                new ArtistSongDto(33L, "Los Angeles", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3", "6:30", 1240000L, "Premium", 3, "The Midnight", "Synthwave"),
                new ArtistSongDto(35L, "Vampires", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3", "5:18", 1120000L, "Basic", 1, "The Midnight", "Synthwave")
            )
        ));

        // art-3: Royalty Free Music Background
        map.put("art-3", new ArtistDetailDto(
            "art-3",
            "Royalty Free Music Background",
            "/images/artists/neon-square.svg",
            "Elektronik ve retro tınıları synthwave ızgarasında harmanlayan bağımsız müzik kolektifi.",
            120823L,
            true,
            List.of(
                new ArtistSongDto(53L, "Cosmic Resonance", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3", "3:15", 420000L, "Basic", 1, "Royalty Free Music Background", "Elektronik")
            )
        ));

        // art-4: Zee-ker
        map.put("art-4", new ArtistDetailDto(
            "art-4",
            "Zee-ker",
            "/images/artists/midnight-sax.svg",
            "Gece sürüşleri ve neon ışıkları için bestelenmiş sıcak saksafon ezgileri ve lofi-synth caz tınıları.",
            310813L,
            true,
            List.of(
                new ArtistSongDto(51L, "Midnight Sax", "https://images.unsplash.com/photo-1511192336575-5a79af67a629?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3", "4:15", 860000L, "Gold", 2, "Zee-ker", "Jazz")
            )
        ));

        // art-5: Lachi James
        map.put("art-5", new ArtistDetailDto(
            "art-5",
            "Lachi James",
            "/images/artists/synthwave-triangle.svg",
            "Modern synthwave ve retro-elektro vokallerin öncüsü. Melankolik ritimler ve neon dalgalar.",
            760472L,
            true,
            List.of(
                new ArtistSongDto(13L, "Midnight (I Want You in My Arms)", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3", "3:42", 1650000L, "Premium", 3, "Lachi James", "Synthwave"),
                new ArtistSongDto(36L, "Neon Horizon", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3", "4:12", 1100000L, "Gold", 2, "Lachi James", "Synthwave"),
                new ArtistSongDto(37L, "Retrograde", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3", "3:55", 920000L, "Basic", 1, "Lachi James", "Synthwave"),
                new ArtistSongDto(38L, "City Lights", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3", "4:05", 780000L, "Premium", 3, "Lachi James", "Synthwave")
            )
        ));

        // art-6: Serene Serene
        map.put("art-6", new ArtistDetailDto(
            "art-6",
            "Serene Serene",
            "/images/artists/cyber-portrait.svg",
            "Siberpunk vizyonu ve eterik synth melodileriyle geleceğin sesini bugüne taşıyan prodüktör.",
            552636L,
            true,
            List.of(
                new ArtistSongDto(52L, "Cyber Lotus", "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-15.mp3", "3:50", 940000L, "Premium", 3, "Serene Serene", "Synthwave")
            )
        ));

        // art-7: Kavinsky
        map.put("art-7", new ArtistDetailDto(
            "art-7",
            "Kavinsky",
            "/images/artists/kavinsky.svg",
            "Fransız house ve synthwave efsanesi. Kırmızı Testarossa'sı ve ikonik 'Nightcall' parçasıyla synthwave kültürünün yaratıcılarından.",
            199068L,
            true,
            List.of(
                new ArtistSongDto(21L, "Nightcall", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "4:19", 5200000L, "Premium", 3, "Kavinsky", "Synthwave"),
                new ArtistSongDto(22L, "Roadgame", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "3:49", 2100000L, "Gold", 2, "Kavinsky", "Elektronik"),
                new ArtistSongDto(24L, "Renegade", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", "3:17", 1350000L, "Elit", 4, "Kavinsky", "Elektronik"),
                new ArtistSongDto(25L, "Pacific Coast Highway", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3", "3:55", 980000L, "Basic", 1, "Kavinsky", "Synthwave")
            )
        ));

        // art-8: Kavinsky, Angèle & Phoenix
        map.put("art-8", new ArtistDetailDto(
            "art-8",
            "Kavinsky, Angèle & Phoenix",
            "/images/artists/nightcall-group.svg",
            "Kavinsky'nin efsanevi melodilerinin Angèle'in zarif vokalleri ve Phoenix grubunun indie-pop dehasıyla buluştuğu yıldızlar karması.",
            464858L,
            true,
            List.of(
                new ArtistSongDto(23L, "Cameo", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3", "4:18", 1850000L, "Premium", 3, "Kavinsky, Angèle & Phoenix", "Elektronik")
            )
        ));

        // art-9: FM-84 & Ollie Wride
        map.put("art-9", new ArtistDetailDto(
            "art-9",
            "FM-84 & Ollie Wride",
            "/images/artists/retro-sun.svg",
            "Colin Hutton (FM-84) ve Ollie Wride birlikteliğiyle ortaya çıkan, 80'lerin en saf yaz rüzgarlarını ve retro synthwave hitlerini yaşatan ikili.",
            983997L,
            true,
            List.of(
                new ArtistSongDto(16L, "Running in the Night", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3", "4:30", 3400000L, "Premium", 3, "FM-84 & Ollie Wride", "Synthwave"),
                new ArtistSongDto(19L, "Bend & Break", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3", "4:36", 1600000L, "Basic", 1, "FM-84 & Ollie Wride", "Synthwave")
            )
        ));

        // art-10: FM-84
        map.put("art-10", new ArtistDetailDto(
            "art-10",
            "FM-84",
            "/images/artists/retro-sun.svg",
            "San Francisco merkezli prodüktör Colin Hutton'ın efsanevi synthwave projesi. 'Atlas' albümü türün başyapıtları arasında yer alır.",
            1617667L,
            true,
            List.of(
                new ArtistSongDto(17L, "Never Stop", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3", "4:48", 2800000L, "Gold", 2, "FM-84", "Synthwave"),
                new ArtistSongDto(20L, "Atlas", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3", "5:51", 2200000L, "Premium", 3, "FM-84", "Synthwave")
            )
        ));

        // art-11: FM-84, Timecop1983 & Josh Dally
        map.put("art-11", new ArtistDetailDto(
            "art-11",
            "FM-84, Timecop1983 & Josh Dally",
            "/images/artists/retro-sun.svg",
            "Synthwave sahnesinin devlerinin bir araya geldiği unutulmaz işbirliği projesi.",
            729879L,
            true,
            List.of(
                new ArtistSongDto(18L, "Wild Ones", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3", "4:28", 1950000L, "Elit", 4, "FM-84, Timecop1983 & Josh Dally", "Synthwave")
            )
        ));

        // art-12: Lazerhawk
        map.put("art-12", new ArtistDetailDto(
            "art-12",
            "Lazerhawk",
            "/images/artists/lazerhawk-car.svg",
            "Outrun ve darksynth türünün öncülerinden Austin'li elektronik müzik ustası. 'Redline' ve 'Skull & Shark' albümleriyle tanınır.",
            1535130L,
            true,
            List.of(
                new ArtistSongDto(26L, "Overdrive", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3", "4:31", 3100000L, "Premium", 3, "Lazerhawk", "Synthwave"),
                new ArtistSongDto(27L, "So Far Away", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3", "4:10", 2200000L, "Gold", 2, "Lazerhawk", "Synthwave"),
                new ArtistSongDto(28L, "King of the Streets", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3", "4:40", 1800000L, "Elit", 4, "Lazerhawk", "Synthwave"),
                new ArtistSongDto(29L, "Electric Groove", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3", "4:45", 1400000L, "Basic", 1, "Lazerhawk", "Synthwave")
            )
        ));

        return map;
    }

    private ArtistDetailDto createDepecheModeDetail(String id) {
        return new ArtistDetailDto(
            id,
            "Depeche Mode",
            "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80",
            "Synth-pop ve elektronik rock tarihinin en etkili gruplarından. 1980'lerden bugüne dark synthwave estetiğinin öncüsü.",
            14500000L,
            true,
            List.of(
                new ArtistSongDto(6L, "Enjoy the Silence", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "4:15", 5800000L, "Elit", 4, "Depeche Mode", "Elektronik"),
                new ArtistSongDto(5L, "Personal Jesus", "https://images.unsplash.com/photo-1493225457124-a1a2a53b51ee?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "3:44", 5100000L, "Premium", 3, "Depeche Mode", "Rock"),
                new ArtistSongDto(1L, "People Are People", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", "3:45", 3900000L, "Premium", 3, "Depeche Mode", "Elektronik"),
                new ArtistSongDto(2L, "Just Can't Get Enough", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3", "3:41", 3400000L, "Premium", 3, "Depeche Mode", "Synthwave"),
                new ArtistSongDto(4L, "Policy of Truth", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3", "4:55", 2800000L, "Premium", 3, "Depeche Mode", "Elektronik"),
                new ArtistSongDto(3L, "Never Let Me Down Again", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3", "4:47", 2600000L, "Premium", 3, "Depeche Mode", "Rock")
            )
        );
    }

    private ArtistDetailDto createDuaLipaDetail(String id) {
        return new ArtistDetailDto(
            id,
            "Dua Lipa",
            "https://images.unsplash.com/photo-1520523839898-507121c21528?w=400&q=80",
            "Modern nu-disco ve synthpop ritimleriyle küresel liste başı ödüllü pop ikonu.",
            78000000L,
            true,
            List.of(
                new ArtistSongDto(14L, "Dance The Night", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "2:56", 8200000L, "Basic", 1, "Dua Lipa", "Pop"),
                new ArtistSongDto(15L, "Houdini", "https://images.unsplash.com/photo-1520523839898-507121c21528?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "3:05", 7400000L, "Premium", 3, "Dua Lipa", "Pop"),
                new ArtistSongDto(39L, "Levitating", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", "3:23", 6900000L, "Gold", 2, "Dua Lipa", "Pop"),
                new ArtistSongDto(40L, "Don't Start Now", "https://images.unsplash.com/photo-1520523839898-507121c21528?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3", "3:03", 6100000L, "Premium", 3, "Dua Lipa", "Pop")
            )
        );
    }

    private ArtistDetailDto createTheWeekndDetail(String id) {
        return new ArtistDetailDto(
            id,
            "The Weeknd",
            "https://images.unsplash.com/photo-1614680376593-902f74cf0d41?w=400&q=80",
            "Karanlık R&B, retro 80'ler synthwave melodileri ve sinematik vokalleriyle çığır açan küresel sanatçı.",
            112000000L,
            true,
            List.of(
                new ArtistSongDto(44L, "Blinding Lights", "https://images.unsplash.com/photo-1614680376593-902f74cf0d41?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "3:20", 9800000L, "Gold", 2, "The Weeknd", "Pop"),
                new ArtistSongDto(45L, "Starboy", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "3:50", 7200000L, "Basic", 1, "The Weeknd", "Pop")
            )
        );
    }

    private List<ArtistSongDto> findSongsForArtist(String artistName) {
        return List.of(
            new ArtistSongDto(1L, "Rezonans Melodisi", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "3:45", 850000L, "Premium", 3, artistName, "Synthwave"),
            new ArtistSongDto(2L, "Ahenk Frekansı", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "4:12", 620000L, "Gold", 2, artistName, "Elektronik")
        );
    }

    private String capitalize(String text) {
        if (text == null || text.isBlank()) return "";
        StringBuilder sb = new StringBuilder();
        for (String word : text.split("\\s+")) {
            if (!word.isBlank()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase(Locale.ROOT))
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
