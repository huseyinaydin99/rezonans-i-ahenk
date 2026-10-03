package tr.com.huseyinaydin.infrastructure.grpc.server;

import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * .proto dosyasından türetilen gRPC servisinin gerçek (Implementation) sınıfı.
 * REST API yerinde durmaya devam ederken, iç servisler bu uç noktaya Binary (İkili) bağlanır.
 */
@Service
public class GrpcRecommendationServiceImpl {
    
    // Gerçekte extends RecommendationServiceGrpc.RecommendationServiceImplBase yapılır
    // ve public void getRecommendations(Request req, StreamObserver<Response> resp) ezilir.

    public void simulateGrpcCall(String userId, int limit) {
        System.out.println("[gRPC SERVER] Gelen ikili (Binary) istek çözümleniyor... Kullanıcı: " + userId);
        
        System.out.println("[gRPC SERVER] Protobuf formatında Yapay Zeka (ML) tavsiyeleri oluşturuldu.");
        System.out.println("[gRPC SERVER] Yanıt, JSON'dan 10 kat daha küçük bir boyutta ışık hızında (Low-Latency) geri gönderiliyor ⚡");
    }
}
