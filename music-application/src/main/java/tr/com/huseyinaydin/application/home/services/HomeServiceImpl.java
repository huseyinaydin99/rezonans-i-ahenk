package tr.com.huseyinaydin.application.home.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.ports.IHomeService;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto.SongSnippet;
import tr.com.huseyinaydin.application.song.queries.GetPersonalizedRecommendationsQuery;

import java.util.List;

@Service
public class HomeServiceImpl implements IHomeService {
    private static final Logger log = LoggerFactory.getLogger(HomeServiceImpl.class);

    private final GetPersonalizedRecommendationsQuery.Handler recommendationHandler;

    public HomeServiceImpl(@Autowired(required = false) GetPersonalizedRecommendationsQuery.Handler recommendationHandler) {
        this.recommendationHandler = recommendationHandler;
    }

    @Override
    @Cacheable(value = "homeDashboardCache", key = "#userId != null ? #userId : 'anonymous'")
    public HomeDashboardDto buildHomeDashboard(String userId) {
        log.info("[ANA SAYFA VİTRİNİ] Dinleyici ('" + userId + "') uygulamaya giriş yaptı. Ana Sayfa panosu yükleniyor...");

        // 1. "Keşfet" Vitrini: Depeche Mode ve ikonik synth/rock klasikleri
        log.info("-> 'Keşfet' küresel parça koleksiyonu derleniyor.");
        List<SongSnippet> topGlobal = List.of(
            new SongSnippet(1L, "People Are People", "Depeche Mode", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "Premium", "Elektronik"),
            new SongSnippet(2L, "Just Can't Get Enough", "Depeche Mode", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "Premium", "Synthwave"),
            new SongSnippet(3L, "Never Let Me Down Again", "Depeche Mode", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "Premium", "Rock"),
            new SongSnippet(4L, "Policy of Truth", "Depeche Mode", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&q=80", "Premium", "Elektronik"),
            new SongSnippet(5L, "Personal Jesus", "Depeche Mode", "https://images.unsplash.com/photo-1493225457124-a1a2a53b51ee?w=400&q=80", "Premium", "Rock"),
            new SongSnippet(6L, "Enjoy the Silence", "Depeche Mode", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400&q=80", "Elit", "Elektronik"),
            new SongSnippet(16L, "Running in the Night", "FM-84 & Ollie Wride", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "Premium", "Synthwave"),
            new SongSnippet(21L, "Nightcall", "Kavinsky", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "Premium", "Synthwave"),
            new SongSnippet(26L, "Overdrive", "Lazerhawk", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "Premium", "Synthwave"),
            new SongSnippet(44L, "Blinding Lights", "The Weeknd", "https://images.unsplash.com/photo-1614680376593-902f74cf0d41?w=400&q=80", "Gold", "Pop")
        );

        // 2. Yeni Çıkanlar (New Releases)
        log.info("-> 'Yeni Çıkanlar' koleksiyonu derleniyor.");
        List<SongSnippet> newReleases = List.of(
            new SongSnippet(15L, "Houdini", "Dua Lipa", "https://images.unsplash.com/photo-1520523839898-507121c21528?w=400&q=80", "Premium", "Pop"),
            new SongSnippet(20L, "Atlas", "FM-84", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "Premium", "Synthwave"),
            new SongSnippet(23L, "Cameo", "Kavinsky, Angèle & Phoenix", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "Premium", "Elektronik"),
            new SongSnippet(36L, "Neon Horizon", "Lachi James", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "Gold", "Synthwave")
        );

        // 3. "Senin İçin Önerilenler": Dinleme geçmişi veya akıllı cold-start önerileri
        log.info("-> 'Senin İçin Önerilenler' kişiselleştirilmiş öneri akışı derleniyor.");
        List<SongSnippet> forYou = null;
        if (recommendationHandler != null) {
            try {
                forYou = recommendationHandler.handle(new GetPersonalizedRecommendationsQuery(userId, 8));
            } catch (Exception ex) {
                log.warn("[ANA SAYFA VİTRİNİ] Öneri motoru çağrısında uyarı (fallback devrede): {}", ex.getMessage());
            }
        }
        if (forYou == null || forYou.isEmpty()) {
            forYou = List.of(
                new SongSnippet(12L, "River of Darkness (feat. Timecop1983)", "The Midnight", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "Premium", "Synthwave"),
                new SongSnippet(13L, "Midnight (I Want You in My Arms)", "Lachi James", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "Premium", "Synthwave"),
                new SongSnippet(14L, "Dance The Night", "Dua Lipa", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "Basic", "Pop"),
                new SongSnippet(15L, "Houdini", "Dua Lipa", "https://images.unsplash.com/photo-1520523839898-507121c21528?w=400&q=80", "Premium", "Pop"),
                new SongSnippet(11L, "Light Years", "The Midnight & Nikki Flores", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "Premium", "Synthwave"),
                new SongSnippet(30L, "Sunset", "The Midnight", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "Gold", "Synthwave"),
                new SongSnippet(39L, "Levitating", "Dua Lipa", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "Gold", "Pop"),
                new SongSnippet(16L, "Running in the Night", "FM-84 & Ollie Wride", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "Premium", "Synthwave")
            );
        }

        log.info("[ANA SAYFA VİTRİNİ] Zengin müzik kataloğu asenkron olarak toparlandı.");
        
        return new HomeDashboardDto(topGlobal, newReleases, forYou);
    }
}
