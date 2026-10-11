package tr.com.huseyinaydin.infrastructure.monitoring.tracing;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Gelen her isteğe benzersiz bir Trace ID atayan ve loglara (MDC) 
 * bu ID'yi basan Dağıtık İzleme (Distributed Tracing) filtresi.
 */
@Component
public class TraceContextFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(TraceContextFilter.class);


    private final Tracer tracer;

    public TraceContextFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            String traceId = currentSpan.context().traceId();
            log.info("[DAĞITIK İZLEME] İstek sisteme girdi. Trace ID: [" + traceId + "]");
            // Kafka, gRPC ve Veritabanı çağrılarının tümü bu eşsiz Trace ID üzerinden
            // Jaeger arayüzünde tek bir zincir (Graph) halinde birleşecek.
            
            exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
        }
        
        return chain.filter(exchange);
    }
}
