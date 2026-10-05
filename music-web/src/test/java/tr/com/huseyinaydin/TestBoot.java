package tr.com.huseyinaydin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import tr.com.huseyinaydin.web.config.MusicWebConfig;

@SpringBootApplication
@Import({tr.com.huseyinaydin.application.config.MusicApplicationConfig.class,
         tr.com.huseyinaydin.infrastructure.config.MusicInfrastructureConfig.class,
         tr.com.huseyinaydin.persistence.config.PersistenceConfig.class,
         tr.com.huseyinaydin.web.config.MusicWebConfig.class})
public class TestBoot {
    public static void main(String[] args) {
        SpringApplication.run(TestBoot.class, args);
    }
}
