package tr.com.huseyinaydin.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.BaggageManager;
import java.util.Map;
import io.micrometer.tracing.ScopedSpan;

import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableCaching
@org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories(basePackages = "tr.com.huseyinaydin.infrastructure")
@ComponentScan(basePackages = "tr.com.huseyinaydin.infrastructure")
@EnableAspectJAutoProxy
public class MusicInfrastructureConfig extends org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration {
    @Override
    public org.springframework.data.elasticsearch.client.ClientConfiguration clientConfiguration() {
        return org.springframework.data.elasticsearch.client.ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();
    }
    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public RouteLocatorBuilder routeLocatorBuilder(ConfigurableApplicationContext context) {
        return new RouteLocatorBuilder(context);
    }

    @Bean
    public Tracer tracer() {
        return io.micrometer.tracing.Tracer.NOOP;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("queryCache", "referenceDataCache");
    }


}
