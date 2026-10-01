package tr.com.huseyinaydin.application.ml.strategies;

import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import java.util.List;
import java.util.UUID;

public interface IRecommendationStrategy {
    List<SongResponse> recommend(UUID userId, int limit);
}
