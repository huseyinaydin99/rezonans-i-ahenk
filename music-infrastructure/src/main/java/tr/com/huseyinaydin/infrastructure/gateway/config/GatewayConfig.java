package tr.com.huseyinaydin.infrastructure.gateway.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mikroservis mimarisinin giriş kapısı olan API Gateway (Ağ Geçidi) Yönlendirme Kuralları.
 */
@Configuration
@org.springframework.context.annotation.Profile("gateway")
public class GatewayConfig {
    private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("[API GATEWAY] Yönlendirme (Routing) kuralları belleğe yükleniyor...");
        
        return builder.routes()
                // 1. Arama İstekleri -> Elasticsearch Servisine (Örnek port 8081)
                .route("search_service_route", r -> r.path("/api/v1/search/**")
                        .uri("http://localhost:8080")) // Gelecekte ayrı mikroservis olacak
                
                // 2. Canlı Bildirimler -> WebSocket Servisine
                .route("websocket_route", r -> r.path("/ws-music/**")
                        .uri("ws://localhost:8080"))
                
                // 3. Medya İstekleri -> MinIO Object Storage (Doğrudan Buluta)
                .route("media_storage_route", r -> r.path("/media/**")
                        .filters(f -> f.rewritePath("/media/(?<segment>.*)", "/music-audio-bucket/${segment}"))
                        .uri("http://localhost:9000"))
                
                // 4. Varsayılan Çekirdek İstekleri (Geriye kalan her şey ana monolite)
                .route("core_service_route", r -> r.path("/**")
                        .uri("http://localhost:8080"))
                .build();
    }
}
