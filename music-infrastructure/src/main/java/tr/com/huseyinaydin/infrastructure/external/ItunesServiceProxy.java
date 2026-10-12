package tr.com.huseyinaydin.infrastructure.external;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(ItunesServiceProxy.class);


    private final IItunesService realService;
    private final IMap<String, ItunesResponseDto> proxyCache;

    @org.springframework.beans.factory.annotation.Autowired
    public ItunesServiceProxy(
            @Qualifier("realItunesService") IItunesService realService,
            @org.springframework.beans.factory.annotation.Autowired(required = false) HazelcastInstance hazelcastInstance) {
        
        this.realService = realService;
        this.proxyCache = hazelcastInstance != null ? hazelcastInstance.getMap("itunes-proxy-cache") : null;
    }

    /**
     * @CircuitBreaker anotasyonu ile iTunes API çökerse sistemi kilitlemek yerine 
     * 'itunesFallback' metoduna yönlendirerek hatayı tolere ediyoruz.
     */
    @Override
    @CircuitBreaker(name = "itunesApi", fallbackMethod = "itunesFallback")
    public ItunesResponseDto searchMusic(String term, int limit) {
        String cacheKey = (term != null ? term.toLowerCase().trim() : "") + "_" + limit;

        if (proxyCache != null && proxyCache.containsKey(cacheKey)) {
            log.info("[PROXY VEKİL] İstek Hazelcast önbelleğinden dönülüyor...");
            return proxyCache.get(cacheKey);
        }

        log.info("[PROXY VEKİL] İstek gerçek iTunes API'sine delege ediliyor...");
        ItunesResponseDto response = realService.searchMusic(term, limit);

        if (proxyCache != null && response != null && response.resultCount() > 0) {
            proxyCache.put(cacheKey, response);
        }

        return response;
    }

    /**
     * FALLBACK (B Planı) Metodu: Devre kesici (Circuit Breaker) AÇIK (Open) konuma geldiğinde 
     * yani karşı sunucu yanıt vermediğinde bizzat burası çalışır.
     */
    public ItunesResponseDto itunesFallback(String term, int limit, Throwable t) {
        log.error("[DEVRE KESİCİ AÇIK - FALLBACK] Dış iTunes API'si çöktü veya ulaşılamıyor! Hata: " + t.getMessage());
        log.info("-> Uygulamanın çökmesi engellendi. Kullanıcıya boş (veya varsayılan) bir yanıt dönülüyor...");
        
        // Hata fırlatmak yerine sistemi ayakta tutacak varsayılan bir veri dönüyoruz.
        return new ItunesResponseDto(0, java.util.Collections.emptyList());
    }
}
