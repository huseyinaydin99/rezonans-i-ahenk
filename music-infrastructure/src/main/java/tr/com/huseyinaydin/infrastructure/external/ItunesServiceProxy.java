package tr.com.huseyinaydin.infrastructure.external;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
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
        // Mevcut Hazelcast dağıtık önbelleğinden iTunes sorguları için özel map alınıyor
        this.proxyCache = hazelcastInstance.getMap("itunes-proxy-cache");
    }

    @Override
    public ItunesResponseDto searchMusic(String term, int limit) {
        String cacheKey = term.toLowerCase().trim() + "_" + limit;

        // 1. Önce vekil (Proxy) kendi belleğini kontrol eder
        if (proxyCache.containsKey(cacheKey)) {
            System.out.println("[PROXY VEKİL] İstek dış dünyaya çıkmadı! Veri doğrudan Hazelcast önbelleğinden (Cache Hit) dönülüyor. Arama: " + term);
            return proxyCache.get(cacheKey);
        }

        // 2. Eğer veri bellekte yoksa, Proxy isteği gerçek servise (Real Subject) delege eder
        System.out.println("[PROXY VEKİL] Veri bellekte bulunamadı. İstek gerçek iTunes API'sine delege ediliyor...");
        ItunesResponseDto response = realService.searchMusic(term, limit);

        // 3. Gerçek servisten dönen sonucu gelecekteki istekler için önbelleğe alır
        if (response != null && response.resultCount() > 0) {
            System.out.println("[PROXY VEKİL] Gerçek servisten gelen yanıt belleğe kaydedildi.");
            proxyCache.put(cacheKey, response);
        }

        return response;
    }
}
