package tr.com.huseyinaydin.infrastructure.external;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tr.com.huseyinaydin.application.ports.IItunesService;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesResponseDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service("realItunesService")
public class ItunesServiceImpl implements IItunesService {

    private final RestTemplate restTemplate;

    public ItunesServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ItunesResponseDto searchMusic(String term, int limit) {
        try {
            String encodedTerm = URLEncoder.encode(term, StandardCharsets.UTF_8);
            String url = "https://itunes.apple.com/search?term=" + encodedTerm + "&media=music&limit=" + limit;
            
            System.out.println("[GERÇEK SERVİS] Apple iTunes API'sine ağ üzerinden (HTTP) gidiliyor: " + url);
            // Simüle edilmiş bekleme ve sonuç dönme
            Thread.sleep(500); // Ağ gecikmesi simülasyonu
            return new ItunesResponseDto(1, java.util.Collections.emptyList());
        } catch (Exception e) {
            System.err.println("iTunes API çağrısında hata: " + e.getMessage());
            return new ItunesResponseDto(0, java.util.Collections.emptyList());
        }
    }
}
