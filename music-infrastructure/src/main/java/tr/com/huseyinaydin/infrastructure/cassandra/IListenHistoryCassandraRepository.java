package tr.com.huseyinaydin.infrastructure.cassandra;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface IListenHistoryCassandraRepository extends CassandraRepository<ListenHistoryEntity, UUID> {
}
