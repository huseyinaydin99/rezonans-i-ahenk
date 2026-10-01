package tr.com.huseyinaydin.application.ml.strategies;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component("genreBasedStrategy")
public class GenreBasedRecommendationStrategy implements IRecommendationStrategy {

    @Override
    public List<SongResponse> recommend(UUID userId, int limit) {
        System.out.println("[ML Strategy] Kullanıcının sevdiği 'Müzik Türlerine' (Genre) göre şarkı öneriliyor...");
        // Veritabanından kullanıcının en çok dinlediği türlere göre analiz yapılıp sonuç dönülür.
        return Collections.emptyList();
    }
}
