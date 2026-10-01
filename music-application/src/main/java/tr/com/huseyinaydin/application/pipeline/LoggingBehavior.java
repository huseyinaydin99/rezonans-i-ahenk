package tr.com.huseyinaydin.application.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.cqrs.IPipelineBehavior;
import tr.com.huseyinaydin.application.cqrs.PipelineDelegate;

@Component
@Order(1) // Runs First
public class LoggingBehavior implements IPipelineBehavior {

    private static final Logger logger = LoggerFactory.getLogger(LoggingBehavior.class);

    @Override
    public <TRequest, TResponse> TResponse handle(TRequest request, PipelineDelegate<TResponse> next) {
        long startTime = System.currentTimeMillis();
        String requestName = request.getClass().getSimpleName();
        
        logger.info("Executing Command/Query: {}", requestName);
        System.out.println("[LOG] Başladı: " + requestName);

        try {
            TResponse response = next.handle();
            long timeTaken = System.currentTimeMillis() - startTime;
            
            if (timeTaken > 500) {
                logger.warn("Performance Warning: {} took {} ms", requestName, timeTaken);
                System.out.println("[PERF_WARN] Yavaş İstek: " + requestName + " (" + timeTaken + " ms)");
            }
            
            logger.info("Successfully executed: {}", requestName);
            return response;
            
        } catch (Exception ex) {
            logger.error("Error executing {}: {}", requestName, ex.getMessage());
            System.err.println("[LOG_ERR] Hata (" + requestName + "): " + ex.getMessage());
            throw ex;
        }
    }
}
