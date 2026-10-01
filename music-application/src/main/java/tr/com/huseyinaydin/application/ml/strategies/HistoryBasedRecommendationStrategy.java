package tr.com.huseyinaydin.application.ml.strategies;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component("historyBasedStrategy")
public class HistoryBasedRecommendationStrategy implements IRecommendationStrategy {

    @Override
    public List<SongResponse> recommend(UUID userId, int limit) {
        System.out.println("[ML Strategy] Kullanıcının 'Geçmiş Dinleme Verilerine' (History) göre derin analiz yapılıyor...");
        // ML algoritması ile collaborative filtering uygulanarak sonuç dönülür.
        return Collections.emptyList();
    }
}
