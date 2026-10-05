package tr.com.huseyinaydin.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.Retry;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreaker oracleCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(5)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(100.0f)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .build();
        return CircuitBreaker.of("oracleDb", config);
    }
    
    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.ofDefaults();
    }
    
    @Bean
    public Retry defaultRetry(RetryRegistry registry) {
        return registry.retry("defaultRetry", RetryConfig.ofDefaults());
    }
}
