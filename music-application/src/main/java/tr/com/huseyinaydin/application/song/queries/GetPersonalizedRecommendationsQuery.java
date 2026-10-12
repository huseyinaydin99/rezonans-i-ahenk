package tr.com.huseyinaydin.application.song.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto.SongSnippet;
import tr.com.huseyinaydin.application.ports.IListeningHistoryRepository;
import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Dinleme geçmişi (ListeningHistory) kayıtlarından kullanıcının müzik frekansını analiz eden,
 * sevdiği türler ve sanatçılarla yüksek benzerliğe sahip henüz dinlemediği parçaları önceliklendiren,
 * dinleme geçmişi bulunmadığında ise "Cold Start" fallback mekanizmasıyla hit parçaları sunan
 * kişiselleştirilmiş öneri CQRS sorgusu.
 */
public record GetPersonalizedRecommendationsQuery(String userEmail, int limit) implements IQuery<List<SongSnippet>> {

    public GetPersonalizedRecommendationsQuery(String userEmail) {
        this(userEmail, 10);
    }

    @Component
    public static class Handler implements IQueryHandler<GetPersonalizedRecommendationsQuery, List<SongSnippet>> {
        private static final Logger log = LoggerFactory.getLogger(GetPersonalizedRecommendationsQuery.class);

        private final IListeningHistoryRepository listeningHistoryRepository;
        private final ISongSearchService songSearchService;

        public Handler(@Autowired(required = false) IListeningHistoryRepository listeningHistoryRepository,
                       @Autowired(required = false) ISongSearchService songSearchService) {
            this.listeningHistoryRepository = listeningHistoryRepository;
            this.songSearchService = songSearchService;
        }

        @Override
        public List<SongSnippet> handle(GetPersonalizedRecommendationsQuery query) {
            String email = (query.userEmail() != null) ? query.userEmail().trim().toLowerCase(Locale.ROOT) : null;
            int limit = query.limit() > 0 ? query.limit() : 10;

            log.info("🔍 [RECOMMENDATION ENGINE] Kullanıcı '{}' için kişisel öneri analizi başlatılıyor (Limit: {})...", email, limit);

            // 1. Dinleme geçmişi kayıtlarını topla
            List<Map<String, Object>> historyRecords = Collections.emptyList();
            if (listeningHistoryRepository != null && email != null && !email.isBlank()) {
                try {
                    historyRecords = listeningHistoryRepository.getRecentHistory(email, 100);
                } catch (Exception ex) {
                    log.warn("[RECOMMENDATION ENGINE] Dinleme geçmişi sorgulanırken uyarı: {}", ex.getMessage());
                }
            }

            // 2. Cold Start Kontrolü: Dinleme geçmişi yoksa akıllı fallback devrede
            if (historyRecords == null || historyRecords.isEmpty()) {
                log.info("❄️ [COLD START] Dinleyici '{}' için dinleme geçmişi bulunamadı. Platform hitleri ve küresel popülerlik fallback'i devreye alınıyor.", email);
                return buildColdStartRecommendations(limit);
            }

            // 3. Geçmişten dinlenen şarkı ID'lerini ve dinleme frekanslarını çıkar
            Map<Long, Integer> listenFrequency = new HashMap<>();
            Set<Long> listenedSongIds = new HashSet<>();

            for (Map<String, Object> record : historyRecords) {
                Object songIdObj = record.get("songId");
                if (songIdObj instanceof Number num) {
                    Long songId = num.longValue();
                    listenedSongIds.add(songId);
                    listenFrequency.merge(songId, 1, Integer::sum);
                }
            }

            if (listenedSongIds.isEmpty()) {
                log.info("❄️ [COLD START] Dinleme geçmişi ayrıştırılamadı. Cold start önerileri sunuluyor.");
                return buildColdStartRecommendations(limit);
            }

            // 4. Dinlenen parçaların tür ve sanatçı ağırlıklarını hesapla
            Map<String, Integer> genreWeights = new HashMap<>();
            Map<String, Integer> artistWeights = new HashMap<>();

            for (Map.Entry<Long, Integer> entry : listenFrequency.entrySet()) {
                Long songId = entry.getKey();
                int weight = entry.getValue();

                Map<String, Object> meta = (songSearchService != null) ? songSearchService.getSongById(songId) : null;
                if (meta != null) {
                    String genre = (String) meta.get("genre");
                    String artist = (String) meta.get("artist");

                    if (genre != null && !genre.isBlank()) {
                        genreWeights.merge(genre.trim(), weight, Integer::sum);
                    }
                    if (artist != null && !artist.isBlank()) {
                        artistWeights.merge(artist.trim(), weight * 2, Integer::sum);
                    }
                }
            }

            log.info("📊 [USER PROFILE] Dinleyici analizi tamamlandı. Ağırlıklı türler: {}, Ağırlıklı sanatçılar: {}",
                    genreWeights.keySet(), artistWeights.keySet());

            // 5. Aday parçaları topla ve skorla
            List<Map<String, Object>> allCatalogSongs = (songSearchService != null)
                    ? songSearchService.searchSongs("")
                    : Collections.emptyList();

            if (allCatalogSongs == null || allCatalogSongs.isEmpty()) {
                return buildColdStartRecommendations(limit);
            }

            List<ScoredSong> scoredCandidates = new ArrayList<>();

            for (Map<String, Object> song : allCatalogSongs) {
                Object idObj = song.get("id");
                if (!(idObj instanceof Number num)) continue;
                Long songId = num.longValue();

                String artist = (String) song.get("artist");
                String genre = (String) song.get("genre");
                String title = (String) song.get("title");
                String cover = (String) song.get("cover");
                String tier = (String) song.get("tier");

                double score = 0.0;
                String reason = null;

                // A: Henüz dinlenmemiş parçalara yüksek öncelik bonusu
                boolean isUnlistened = !listenedSongIds.contains(songId);
                if (isUnlistened) {
                    score += 50.0;
                } else {
                    score -= 30.0; // Daha önce dinlenmişse geriye it
                }

                // B: Sanatçı benzerlik skoru
                if (artist != null && artistWeights.containsKey(artist)) {
                    int w = artistWeights.get(artist);
                    score += (w * 15.0);
                    reason = "Sevdiğin sanatçı: " + artist;
                }

                // C: Tür benzerlik skoru
                if (genre != null && genreWeights.containsKey(genre)) {
                    int gw = genreWeights.get(genre);
                    score += (gw * 8.0);
                    if (reason == null) {
                        reason = "En çok dinlediğin #" + genre.toUpperCase(Locale.ROOT) + " türünde";
                    }
                }

                // D: Küresel dinlenme sayısı (Popülerlik sinyali)
                long playCount = extractPlayCount(song);
                score += (Math.log10(playCount + 10.0) * 3.0);

                if (reason == null) {
                    reason = isUnlistened ? "Frekansına uyum sağlayan yeni keşif" : "Tekrar dinlemek isteyebileceğin parça";
                }

                scoredCandidates.add(new ScoredSong(
                        new SongSnippet(songId, title, artist, cover, tier, genre, playCount, reason),
                        score
                ));
            }

            // 6. Skora göre azalan sırala ve istenen limitte döndür
            List<SongSnippet> finalRecommendations = scoredCandidates.stream()
                    .sorted(Comparator.comparingDouble(ScoredSong::score).reversed())
                    .limit(limit)
                    .map(ScoredSong::snippet)
                    .collect(Collectors.toList());

            if (finalRecommendations.isEmpty()) {
                return buildColdStartRecommendations(limit);
            }

            log.info("🎯 [RECOMMENDATIONS READY] '{}' için {} adet kişiselleştirilmiş öneri başarıyla üretildi.",
                    email, finalRecommendations.size());

            return finalRecommendations;
        }

        private List<SongSnippet> buildColdStartRecommendations(int limit) {
            List<Map<String, Object>> catalog = (songSearchService != null)
                    ? songSearchService.searchSongs("")
                    : Collections.emptyList();

            if (catalog != null && !catalog.isEmpty()) {
                return catalog.stream()
                        .sorted((a, b) -> {
                            long countA = extractPlayCount(a);
                            long countB = extractPlayCount(b);
                            return Long.compare(countB, countA);
                        })
                        .limit(limit)
                        .map(song -> {
                            Long songId = ((Number) song.get("id")).longValue();
                            long playCount = extractPlayCount(song);
                            return new SongSnippet(
                                    songId,
                                    (String) song.get("title"),
                                    (String) song.get("artist"),
                                    (String) song.get("cover"),
                                    (String) song.get("tier"),
                                    (String) song.get("genre"),
                                    playCount,
                                    "Platformun en çok dinlenen hit parçası"
                            );
                        })
                        .collect(Collectors.toList());
            }

            // Dayanıklılık (Resilience) için Statik Küresel Fallback
            return List.of(
                    new SongSnippet(12L, "River of Darkness (feat. Timecop1983)", "The Midnight", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "Premium", "Synthwave", 2450000L, "Platformun en çok dinlenen hit parçası"),
                    new SongSnippet(6L, "Enjoy the Silence", "Depeche Mode", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400&q=80", "Elit", "Elektronik", 3890000L, "Platformun en çok dinlenen hit parçası"),
                    new SongSnippet(16L, "Running in the Night", "FM-84 & Ollie Wride", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "Premium", "Synthwave", 2150000L, "Platformun en çok dinlenen hit parçası"),
                    new SongSnippet(21L, "Nightcall", "Kavinsky", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "Premium", "Synthwave", 2980000L, "Platformun en çok dinlenen hit parçası"),
                    new SongSnippet(30L, "Sunset", "The Midnight", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "Gold", "Synthwave", 2310000L, "Platformun en çok dinlenen hit parçası"),
                    new SongSnippet(39L, "Levitating", "Dua Lipa", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "Gold", "Pop", 3120000L, "Platformun en çok dinlenen hit parçası"),
                    new SongSnippet(44L, "Blinding Lights", "The Weeknd", "https://images.unsplash.com/photo-1614680376593-902f74cf0d41?w=400&q=80", "Gold", "Pop", 4500000L, "Platformun en çok dinlenen hit parçası")
            );
        }

        private long extractPlayCount(Map<String, Object> song) {
            if (song == null) return 0L;
            Object pc = song.get("playCount");
            if (pc instanceof Number n && n.longValue() > 0) {
                return n.longValue();
            }
            Object idObj = song.get("id");
            if (idObj instanceof Number idNum) {
                Long songId = idNum.longValue();
                if (listeningHistoryRepository != null) {
                    return listeningHistoryRepository.getPlayCount(songId);
                }
                return 1000000L + (songId * 150000L);
            }
            return 1000000L;
        }

        private record ScoredSong(SongSnippet snippet, double score) {}
    }
}
