package tr.com.huseyinaydin.infrastructure.grpc.client;

import org.springframework.stereotype.Component;

/**
 * Ana uygulamanın, Yapay Zeka veya Arama servisine REST yerine gRPC ile bağlanmasını sağlayan istemci.
 */
@Component
public class InternalRecommendationClient {

    private final GrpcRecommendationServiceImpl grpcServiceMock;

    public InternalRecommendationClient(GrpcRecommendationServiceImpl grpcServiceMock) {
        this.grpcServiceMock = grpcServiceMock;
    }

    public void fetchRecommendations(String userId) {
        System.out.println("[gRPC CLIENT] İç servisler arası haberleşme başlıyor (REST JSON atlandı)!");
        // Gerçekte ManagedChannelBuilder ile port 9090'a bağlanılır.
        grpcServiceMock.simulateGrpcCall(userId, 5);
    }
}
