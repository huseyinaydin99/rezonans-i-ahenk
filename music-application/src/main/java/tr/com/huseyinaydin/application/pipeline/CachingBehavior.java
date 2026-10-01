package tr.com.huseyinaydin.application.pipeline;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.cqrs.IPipelineBehavior;
import tr.com.huseyinaydin.application.cqrs.PipelineDelegate;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(3) // Runs after Logging and Auth
public class CachingBehavior implements IPipelineBehavior {

    // Simulating an In-Memory Cache or Redis Client
    private final ConcurrentHashMap<String, Object> cacheStore = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <TRequest, TResponse> TResponse handle(TRequest request, PipelineDelegate<TResponse> next) {
        if (request instanceof ICacheableRequest cacheable) {
            String cacheKey = cacheable.getCacheKey();
            
            if (cacheStore.containsKey(cacheKey)) {
                System.out.println("Cache Hit! Key: " + cacheKey);
                return (TResponse) cacheStore.get(cacheKey);
            }
            
            System.out.println("Cache Miss. Executing handler for Key: " + cacheKey);
            TResponse response = next.handle();
            cacheStore.put(cacheKey, response);
            return response;
        }
        
        return next.handle();
    }
}
