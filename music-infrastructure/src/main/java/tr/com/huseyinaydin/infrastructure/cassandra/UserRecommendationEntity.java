package tr.com.huseyinaydin.infrastructure.cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Apache Cassandra Entity for precalculated Apache Spark Machine Learning Recommendations.
 * Stores ALS / Collaborative Filtering model output for high-speed, sub-millisecond query access.
 */
@Table("user_recommendations")
public class UserRecommendationEntity {

    @PrimaryKey
    private UUID userId;

    @Column("recommended_song_ids")
    private List<UUID> recommendedSongIds = new ArrayList<>();

    @Column("algorithm_name")
    private String algorithmName;

    @Column("score")
    private Double score;

    @Column("updated_at")
    private Instant updatedAt;

    public UserRecommendationEntity() {
    }

    public UserRecommendationEntity(UUID userId, List<UUID> recommendedSongIds, String algorithmName, Double score, Instant updatedAt) {
        this.userId = userId;
        this.recommendedSongIds = recommendedSongIds != null ? recommendedSongIds : new ArrayList<>();
        this.algorithmName = algorithmName;
        this.score = score;
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public UUID getUserId() { 
        return userId; 
    }

    public void setUserId(UUID userId) { 
        this.userId = userId; 
    }

    public List<UUID> getRecommendedSongIds() { 
        return recommendedSongIds; 
    }

    public void setRecommendedSongIds(List<UUID> recommendedSongIds) { 
        this.recommendedSongIds = recommendedSongIds; 
    }

    public String getAlgorithmName() { 
        return algorithmName; 
    }

    public void setAlgorithmName(String algorithmName) { 
        this.algorithmName = algorithmName; 
    }

    public Double getScore() { 
        return score; 
    }

    public void setScore(Double score) { 
        this.score = score; 
    }

    public Instant getUpdatedAt() { 
        return updatedAt; 
    }

    public void setUpdatedAt(Instant updatedAt) { 
        this.updatedAt = updatedAt; 
    }
}
