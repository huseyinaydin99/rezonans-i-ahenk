package tr.com.huseyinaydin.infrastructure.external;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.ports.IItunesService;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesResponseDto;

@Primary
@Service("itunesServiceProxy")
public class ItunesServiceProxy implements IItunesService {

    private final IItunesService realService;
    private final IMap<String, ItunesResponseDto> proxyCache;

    public ItunesServiceProxy(
            @Qualifier("realItunesService") IItunesService realService,
            HazelcastInstance hazelcastInstance) {
        
        this.realService = realService;
        this.proxyCache = hazelcastInstance.getMap("itunes-proxy-cache");
    }

    /**
     * @CircuitBreaker anotasyonu ile iTunes API çökerse sistemi kilitlemek yerine 
     * 'itunesFallback' metoduna yönlendirerek hatayı tolere ediyoruz.
     */
    @Override
    @CircuitBreaker(name = "itunesApi", fallbackMethod = "itunesFallback")
    public ItunesResponseDto searchMusic(String term, int limit) {
        String cacheKey = term.toLowerCase().trim() + "_" + limit;

        if (proxyCache.containsKey(cacheKey)) {
            System.out.println("[PROXY VEKİL] İstek Hazelcast önbelleğinden dönülüyor...");
            return proxyCache.get(cacheKey);
        }

        System.out.println("[PROXY VEKİL] İstek gerçek iTunes API'sine delege ediliyor...");
        ItunesResponseDto response = realService.searchMusic(term, limit);

        if (response != null && response.resultCount() > 0) {
            proxyCache.put(cacheKey, response);
        }

        return response;
    }

    /**
     * FALLBACK (B Planı) Metodu: Devre kesici (Circuit Breaker) AÇIK (Open) konuma geldiğinde 
     * yani karşı sunucu yanıt vermediğinde bizzat burası çalışır.
     */
    public ItunesResponseDto itunesFallback(String term, int limit, Throwable t) {
        System.err.println("[DEVRE KESİCİ AÇIK - FALLBACK] Dış iTunes API'si çöktü veya ulaşılamıyor! Hata: " + t.getMessage());
        System.out.println("-> Uygulamanın çökmesi engellendi. Kullanıcıya boş (veya varsayılan) bir yanıt dönülüyor...");
        
        // Hata fırlatmak yerine sistemi ayakta tutacak varsayılan bir veri dönüyoruz.
        return new ItunesResponseDto(0, java.util.Collections.emptyList());
    }
}
