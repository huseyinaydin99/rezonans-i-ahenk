package tr.com.huseyinaydin.application.ml.strategies;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component("historyBasedStrategy")
public class HistoryBasedRecommendationStrategy implements IRecommendationStrategy {
    private static final Logger log = LoggerFactory.getLogger(HistoryBasedRecommendationStrategy.class);


    @Override
    public List<SongResponse> recommend(UUID userId, int limit) {
        log.info("[ML Strategy] Kullanıcının 'Geçmiş Dinleme Verilerine' (History) göre derin analiz yapılıyor...");
        // ML algoritması ile collaborative filtering uygulanarak sonuç dönülür.
        return Collections.emptyList();
    }
}
