package tr.com.huseyinaydin.application.pipeline;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.cqrs.IPipelineBehavior;
import tr.com.huseyinaydin.application.cqrs.PipelineDelegate;

@Component
@Order(3) // Runs after Logging and Validation
public class CachingBehavior implements IPipelineBehavior {

    private final IMap<String, Object> hazelcastCacheMap;

    public CachingBehavior(HazelcastInstance hazelcastInstance) {
        // Altyapıdan gelen Hazelcast örneği üzerinden dağıtık haritayı alıyoruz
        this.hazelcastCacheMap = hazelcastInstance.getMap("cqrs-cache");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TRequest, TResponse> TResponse handle(TRequest request, PipelineDelegate<TResponse> next) {
        
        // İstek önbelleklenebilir bir sorgu ise devreye gir
        if (request instanceof ICacheableRequest cacheable) {
            String cacheKey = cacheable.getCacheKey();
            
            // Hazelcast Cluster üzerinden veriyi kontrol et
            if (hazelcastCacheMap.containsKey(cacheKey)) {
                System.out.println("[HAZELCAST CACHE HIT] Veri bellekten okunuyor. Key: " + cacheKey);
                return (TResponse) hazelcastCacheMap.get(cacheKey);
            }
            
            System.out.println("[HAZELCAST CACHE MISS] Veritabanına gidiliyor. Key: " + cacheKey);
            TResponse response = next.handle(); // Gerçek Handler çalışır (DB'ye gider)
            
            // Sonucu Hazelcast kümesine yaz (Dağıtık mimaride tüm sunucular görür)
            hazelcastCacheMap.put(cacheKey, response);
            return response;
        }
        
        return next.handle();
    }
}
