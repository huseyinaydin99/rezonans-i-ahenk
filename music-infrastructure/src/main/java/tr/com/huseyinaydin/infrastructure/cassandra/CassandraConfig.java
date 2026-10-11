package tr.com.huseyinaydin.infrastructure.cassandra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.Collections;
import java.util.List;

@Configuration
@Conditional(CassandraAvailableCondition.class)
@EnableCassandraRepositories(basePackages = "tr.com.huseyinaydin.infrastructure.cassandra")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.cassandra.local-datacenter:dc1}")
    private String localDataCenter;

    @Value("${spring.cassandra.contact-points:localhost}")
    private String contactPoints;

    @Value("${spring.cassandra.port:9042}")
    private int port;

    @Value("${spring.cassandra.keyspace-name:music_keyspace}")
    private String keyspaceName;

    @Override
    protected String getKeyspaceName() {
        return keyspaceName != null && !keyspaceName.isBlank() ? keyspaceName : "music_keyspace";
    }

    @Override
    protected String getLocalDataCenter() {
        return localDataCenter != null && !localDataCenter.isBlank() ? localDataCenter : "dc1";
    }

    @Override
    protected String getContactPoints() {
        return contactPoints != null && !contactPoints.isBlank() ? contactPoints : "localhost";
    }

    @Override
    protected int getPort() {
        return port > 0 ? port : 9042;
    }

    @Override
    public SchemaAction getSchemaAction() {
        // Tablo yoksa otomatik oluşturur (Entity bazında)
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        CreateKeyspaceSpecification specification = CreateKeyspaceSpecification
                .createKeyspace(getKeyspaceName())
                .ifNotExists()
                .with(KeyspaceOption.DURABLE_WRITES, true)
                .withSimpleReplication();
        return Collections.singletonList(specification);
    }
}
