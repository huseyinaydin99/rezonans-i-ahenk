package tr.com.huseyinaydin.application.ml.strategies;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component("popularityBasedStrategy")
public class PopularityBasedRecommendationStrategy implements IRecommendationStrategy {

    @Override
    public List<SongResponse> recommend(UUID userId, int limit) {
        System.out.println("[ML Strategy] Genel 'Trend ve Popülerliğe' (Global Top Charts) göre öneri yapılıyor...");
        // Eğer kullanıcı yeniyse (cold start) en popüler şarkılar dönülür.
        return Collections.emptyList();
    }
}
