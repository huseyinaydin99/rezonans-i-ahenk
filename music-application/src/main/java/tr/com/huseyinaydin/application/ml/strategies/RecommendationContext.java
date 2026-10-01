package tr.com.huseyinaydin.application.ml.strategies;

import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationContext {

    private IRecommendationStrategy strategy;

    // Varsayılan Strateji Ataması
    public RecommendationContext() {
        this.strategy = new PopularityBasedRecommendationStrategy();
    }

    // Çalışma zamanında (Runtime) algoritmayı değiştirme imkanı
    public void setStrategy(IRecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public List<SongResponse> executeRecommendation(UUID userId, int limit) {
        if (this.strategy == null) {
            throw new IllegalStateException("Tavsiye stratejisi belirlenmedi!");
        }
        return strategy.recommend(userId, limit);
    }
}
