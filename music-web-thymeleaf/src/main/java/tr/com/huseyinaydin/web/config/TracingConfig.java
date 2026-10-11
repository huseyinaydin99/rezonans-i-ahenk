package tr.com.huseyinaydin.web.config;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.OtelBaggageManager;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import java.util.Collections;
import java.util.Properties;

/**
 * Dağıtık İzleme (Distributed Tracing) Konfigürasyonu.
 * Jaeger, Zipkin ve OpenTelemetry (OTLP) entegrasyonlarını sağlar.
 */
@Configuration
@org.springframework.context.annotation.PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
public class TracingConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer yamlProperties() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        Properties properties = yaml.getObject();
        if (properties != null) {
            configurer.setProperties(properties);
        }
        configurer.setLocations(new ClassPathResource("application.properties"));
        configurer.setLocalOverride(true);
        configurer.setIgnoreResourceNotFound(true);
        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }

    @Bean
    public OpenTelemetry openTelemetry(Environment env) {
        boolean disabled = env.getProperty("otel.sdk.disabled", Boolean.class, false);
        if (disabled) {
            return OpenTelemetry.noop();
        }

        String serviceName = env.getProperty("otel.service.name", "rezonans-music-platform");
        String otlpEndpoint = env.getProperty("otel.exporter.otlp.endpoint", "http://localhost:4317");
        String zipkinEndpoint = env.getProperty("management.zipkin.tracing.endpoint", "http://localhost:9411/api/v2/spans");

        Resource resource = Resource.getDefault().merge(
                Resource.create(Attributes.of(
                        AttributeKey.stringKey("service.name"), serviceName,
                        AttributeKey.stringKey("service.version"), "1.0.0"
                )));

        OtlpGrpcSpanExporter otlpSpanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(otlpEndpoint)
                .build();

        ZipkinSpanExporter zipkinSpanExporter = ZipkinSpanExporter.builder()
                .setEndpoint(zipkinEndpoint)
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(BatchSpanProcessor.builder(otlpSpanExporter).build())
                .addSpanProcessor(BatchSpanProcessor.builder(zipkinSpanExporter).build())
                .build();

        ContextPropagators propagators = ContextPropagators.create(
                TextMapPropagator.composite(
                        W3CTraceContextPropagator.getInstance(),
                        W3CBaggagePropagator.getInstance()
                )
        );

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagators(propagators)
                .build();
    }

    @Bean
    @Primary
    public Tracer tracer(OpenTelemetry openTelemetry) {
        io.opentelemetry.api.trace.Tracer otelTracer = openTelemetry.getTracer("rezonans-music-platform");
        OtelCurrentTraceContext otelCurrentTraceContext = new OtelCurrentTraceContext();
        OtelBaggageManager otelBaggageManager = new OtelBaggageManager(
                otelCurrentTraceContext,
                Collections.emptyList(),
                Collections.emptyList()
        );

        return new OtelTracer(
                otelTracer,
                otelCurrentTraceContext,
                event -> {},
                otelBaggageManager
        );
    }
}
