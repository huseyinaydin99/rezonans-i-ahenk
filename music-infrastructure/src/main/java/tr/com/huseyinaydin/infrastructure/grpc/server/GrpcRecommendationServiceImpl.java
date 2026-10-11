package tr.com.huseyinaydin.infrastructure.grpc.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * .proto dosyasından türetilen gRPC servisinin gerçek (Implementation) sınıfı.
 * REST API yerinde durmaya devam ederken, iç servisler bu uç noktaya Binary (İkili) bağlanır.
 */
@Service
public class GrpcRecommendationServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(GrpcRecommendationServiceImpl.class);

    
    // Gerçekte extends RecommendationServiceGrpc.RecommendationServiceImplBase yapılır
    // ve public void getRecommendations(Request req, StreamObserver<Response> resp) ezilir.

    public void simulateGrpcCall(String userId, int limit) {
        log.info("[gRPC SERVER] Gelen ikili (Binary) istek çözümleniyor... Kullanıcı: " + userId);
        
        log.info("[gRPC SERVER] Protobuf formatında Yapay Zeka (ML) tavsiyeleri oluşturuldu.");
        log.info("[gRPC SERVER] Yanıt, JSON'dan 10 kat daha küçük bir boyutta ışık hızında (Low-Latency) geri gönderiliyor ⚡");
    }
}
