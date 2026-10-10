package tr.com.huseyinaydin.infrastructure.cassandra;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = "tr.com.huseyinaydin.infrastructure.cassandra")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Override
    protected String getKeyspaceName() {
        return "music_keyspace";
    }

    @Override
    public SchemaAction getSchemaAction() {
        // Tablo yoksa otomatik oluşturur (Entity bazında)
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }
}
