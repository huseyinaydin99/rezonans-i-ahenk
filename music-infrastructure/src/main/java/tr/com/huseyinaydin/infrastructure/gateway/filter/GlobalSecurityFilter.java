package tr.com.huseyinaydin.infrastructure.gateway.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gelen tüm istekleri ilk karşılayan ve mikroservislere inmeden önce
 * yetki (JWT) veya loglama kontrolleri yapan Global Filtre.
 */
@Component
public class GlobalSecurityFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(GlobalSecurityFilter.class);


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("[API GATEWAY - GÜVENLİK FİLTRESİ] Yeni istek yakalandı: " 
                + request.getMethod() + " " + request.getURI().getPath());
        
        // Örn: İstek başlığında "Authorization" yoksa veya geçersizse Gateway seviyesinde reddedilebilir
        // (Simülasyon amacı taşıdığından doğrudan akışa izin veriyoruz)
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("[API GATEWAY - YANIT] İstek mikroservislerden başarıyla döndü.");
        }));
    }

    @Override
    public int getOrder() {
        return -1; // En yüksek öncelik, herkesten önce çalışır
    }
}
