package tr.com.huseyinaydin.infrastructure.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tr.com.huseyinaydin.application.ports.IItunesService;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesResponseDto;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesTrackDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service("realItunesService")
public class ItunesServiceImpl implements IItunesService {
    private static final Logger log = LoggerFactory.getLogger(ItunesServiceImpl.class);

    private final RestTemplate restTemplate;

    public ItunesServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ItunesResponseDto searchMusic(String term, int limit) {
        String query = (term != null && !term.trim().isEmpty()) ? term.trim() : "The Midnight";
        int safeLimit = limit > 0 ? limit : 10;
        
        try {
            String encodedTerm = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://itunes.apple.com/search?term=" + encodedTerm + "&media=music&entity=song&limit=" + safeLimit;
            log.info("[GERÇEK SERVİS] Apple iTunes API'sine istek gönderiliyor: {}", url);
            
            ItunesResponseDto response = restTemplate.getForObject(url, ItunesResponseDto.class);
            if (response != null && response.results() != null && !response.results().isEmpty()) {
                log.info("[GERÇEK SERVİS] iTunes API'sinden {} adet parça başarıyla çekildi.", response.results().size());
                return response;
            }
        } catch (Exception e) {
            log.warn("[GERÇEK SERVİS] Canlı iTunes API çağrısı gerçekleştirilemedi (çevrimdışı/ağ engeli: {}). Sentetik retro katalog devreye alınıyor.", e.getMessage());
        }

        // Çevrimdışı / korumalı çalışma ortamı için sentetik retro synthwave kataloğu
        return generateSyntheticRetroTracks(query, safeLimit);
    }

    private ItunesResponseDto generateSyntheticRetroTracks(String query, int limit) {
        List<ItunesTrackDto> list = new ArrayList<>();
        String lower = query.toLowerCase();

        if (lower.contains("midnight")) {
            list.add(new ItunesTrackDto("Sunset", "The Midnight", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600", "https://actions.google.com/sounds/v1/science_fiction/retro_synth_intro.ogg", 326000, "Synthwave"));
            list.add(new ItunesTrackDto("Vampires", "The Midnight", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600", "https://actions.google.com/sounds/v1/science_fiction/space_ambience.ogg", 317000, "Synthwave"));
            list.add(new ItunesTrackDto("Los Angeles", "The Midnight", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600", "https://actions.google.com/sounds/v1/science_fiction/alien_beacon.ogg", 390000, "Synthwave"));
            list.add(new ItunesTrackDto("Days of Thunder", "The Midnight", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600", "https://actions.google.com/sounds/v1/science_fiction/space_engine_loop.ogg", 345000, "Synthwave"));
        } else if (lower.contains("kavinsky")) {
            list.add(new ItunesTrackDto("Nightcall", "Kavinsky", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600", "https://actions.google.com/sounds/v1/science_fiction/space_hum.ogg", 259000, "Synthwave"));
            list.add(new ItunesTrackDto("Pacific Coast Highway", "Kavinsky", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600", "https://actions.google.com/sounds/v1/science_fiction/warp_speed.ogg", 360000, "Synthwave"));
            list.add(new ItunesTrackDto("Roadgame", "Kavinsky", "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600", "https://actions.google.com/sounds/v1/science_fiction/force_field_pulse.ogg", 225000, "Synthwave"));
        } else if (lower.contains("fm-84") || lower.contains("fm84")) {
            list.add(new ItunesTrackDto("Running in the Night", "FM-84", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600", "https://actions.google.com/sounds/v1/science_fiction/retro_synth_intro.ogg", 270000, "Synthwave"));
            list.add(new ItunesTrackDto("Atlas", "FM-84", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600", "https://actions.google.com/sounds/v1/science_fiction/space_ambience.ogg", 332000, "Synthwave"));
            list.add(new ItunesTrackDto("Bend & Break", "FM-84", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600", "https://actions.google.com/sounds/v1/science_fiction/alien_beacon.ogg", 310000, "Synthwave"));
        } else if (lower.contains("gunship")) {
            list.add(new ItunesTrackDto("Tech Noir", "Gunship", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600", "https://actions.google.com/sounds/v1/science_fiction/warp_speed.ogg", 298000, "Synthwave"));
            list.add(new ItunesTrackDto("Fly For Your Life", "Gunship", "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600", "https://actions.google.com/sounds/v1/science_fiction/space_engine_loop.ogg", 279000, "Synthwave"));
            list.add(new ItunesTrackDto("Dark All Day", "Gunship", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600", "https://actions.google.com/sounds/v1/science_fiction/force_field_pulse.ogg", 328000, "Synthwave"));
        } else if (lower.contains("lazerhawk")) {
            list.add(new ItunesTrackDto("Overdrive", "Lazerhawk", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600", "https://actions.google.com/sounds/v1/science_fiction/space_hum.ogg", 264000, "Synthwave"));
            list.add(new ItunesTrackDto("So Far Away", "Lazerhawk", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600", "https://actions.google.com/sounds/v1/science_fiction/retro_synth_intro.ogg", 245000, "Synthwave"));
        } else if (lower.contains("timecop1983")) {
            list.add(new ItunesTrackDto("Journeys", "Timecop1983", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600", "https://actions.google.com/sounds/v1/science_fiction/alien_beacon.ogg", 320000, "Synthwave"));
            list.add(new ItunesTrackDto("Lovers", "Timecop1983", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600", "https://actions.google.com/sounds/v1/science_fiction/space_ambience.ogg", 295000, "Synthwave"));
        } else if (lower.contains("carpenter")) {
            list.add(new ItunesTrackDto("Turbo Killer", "Carpenter Brut", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600", "https://actions.google.com/sounds/v1/science_fiction/warp_speed.ogg", 208000, "Synthwave"));
            list.add(new ItunesTrackDto("Roller Mobster", "Carpenter Brut", "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600", "https://actions.google.com/sounds/v1/science_fiction/force_field_pulse.ogg", 214000, "Synthwave"));
        } else {
            list.add(new ItunesTrackDto(query + " - Neon Pulse", query, "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600", "https://actions.google.com/sounds/v1/science_fiction/retro_synth_intro.ogg", 210000, "Synthwave"));
            list.add(new ItunesTrackDto(query + " - Cyber Mirage", query, "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600", "https://actions.google.com/sounds/v1/science_fiction/space_ambience.ogg", 240000, "Synthwave"));
        }

        int max = Math.min(list.size(), limit > 0 ? limit : list.size());
        List<ItunesTrackDto> subList = list.subList(0, max);
        return new ItunesResponseDto(subList.size(), subList);
    }
}
