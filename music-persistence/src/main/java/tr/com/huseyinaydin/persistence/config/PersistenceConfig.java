package tr.com.huseyinaydin.persistence.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@org.springframework.context.annotation.PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@org.springframework.context.annotation.ComponentScan(basePackages = "tr.com.huseyinaydin.persistence")
@org.springframework.transaction.annotation.EnableTransactionManagement
public class PersistenceConfig {


    @Autowired
    public void setApplicationContext(org.springframework.context.ApplicationContext context) {
        tr.com.huseyinaydin.persistence.audit.JpaAuditEntityListener.setApplicationContext(context);
    }


    @Autowired
    private Environment env;

    @Value("${spring.datasource.url:jdbc:oracle:thin:@localhost:1521/FREEPDB1}")
    private String dbUrl;

    @Value("${spring.datasource.username:music_app}")
    private String dbUsername;

    @Value("${spring.datasource.password:music_app_pwd}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name:oracle.jdbc.OracleDriver}")
    private String driverClassName;

    @Bean
    @org.springframework.context.annotation.Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setDriverClassName(driverClassName);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setAutoCommit(false);
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30_000);
        config.setIdleTimeout(600_000);
        config.setMaxLifetime(1_800_000);
        if (driverClassName.contains("Oracle") || dbUrl.contains("oracle")) {
            config.setConnectionTestQuery("SELECT 1 FROM DUAL");
        }
        return new HikariDataSource(config);
    }

    @Bean
    public org.springframework.jdbc.core.JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new org.springframework.jdbc.core.JdbcTemplate(dataSource);
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return org.springframework.orm.jpa.SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setMappingResources(
            "META-INF/orm/BaseEntity.xml",
            "META-INF/orm/Entity.xml",
            "META-INF/orm/Customer.xml",
            "META-INF/orm/IndividualCustomer.xml",
            "META-INF/orm/CorporateCustomer.xml",
            "META-INF/orm/CreditType.xml",
            "META-INF/orm/CreditApplication.xml",
            "META-INF/orm/ApplicationUser.xml",
            "META-INF/orm/RefreshToken.xml",
            "META-INF/orm/AuditLog.xml",
            "META-INF/orm/OperationClaim.xml",
            "META-INF/orm/UserOperationClaim.xml",
            "META-INF/orm/Song.xml",
            "META-INF/orm/Artist.xml",
            "META-INF/orm/DomainEventOutbox.xml",
            "META-INF/orm/SongReadModel.xml"
        );
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaProperties(hibernateProperties());
        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        
        String dialect = env.getProperty("spring.jpa.database-platform", 
            env.getProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.OracleDialect"));
        props.setProperty("hibernate.dialect", dialect);

        String ddlAuto = env.getProperty("spring.jpa.hibernate.ddl-auto", "update");
        props.setProperty("hibernate.hbm2ddl.auto", ddlAuto);

        String showSql = env.getProperty("spring.jpa.show-sql", "true");
        props.setProperty("hibernate.show_sql", showSql);
        
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.jdbc.batch_size", "0");
        props.setProperty("hibernate.order_inserts", "false");
        props.setProperty("hibernate.order_updates", "false");
        props.setProperty("hibernate.jdbc.batch_versioned_data", "false");
        props.setProperty("hibernate.id.new_generator_mappings", "true");
        props.setProperty("hibernate.id.sequence.default_allocation_size", "50");
        props.setProperty("hibernate.generate_statistics", "false");
        return props;
    }
}
