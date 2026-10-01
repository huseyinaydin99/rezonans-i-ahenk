package tr.com.huseyinaydin.application.ports;

import java.util.List;
import java.util.UUID;

public interface IMusicRecommendationService {
    List<UUID> getRecommendationsForUser(UUID userId, int limit);
}
