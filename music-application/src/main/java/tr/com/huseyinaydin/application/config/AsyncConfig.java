package tr.com.huseyinaydin.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Spring Boot'un @Async anotasyonlarını devreye alır
}
