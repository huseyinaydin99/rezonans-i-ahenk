package tr.com.huseyinaydin.infrastructure.external;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tr.com.huseyinaydin.application.ports.IItunesService;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesResponseDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
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
            
            // Gerçek iTunes API'sine istek atıyoruz
            System.out.println("Calling external API: " + url);
            return restTemplate.getForObject(url, ItunesResponseDto.class);
        } catch (Exception e) {
            System.err.println("iTunes API çağrısında hata: " + e.getMessage());
            // Hata durumunda boş DTO dönüyoruz
            return new ItunesResponseDto(0, java.util.Collections.emptyList());
        }
    }
}
