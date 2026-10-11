package tr.com.huseyinaydin.infrastructure.cassandra;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data Cassandra repository for Spark-computed user recommendations.
 */
@Repository
public interface IUserRecommendationCassandraRepository extends CassandraRepository<UserRecommendationEntity, UUID> {
}
