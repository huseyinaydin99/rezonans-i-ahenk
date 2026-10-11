package tr.com.huseyinaydin.web.monitoring;

import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.OtelBaggageManager;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ObservabilityMetricsAndTracingTest {

    private PrometheusMeterRegistry prometheusRegistry;
    private MusicPlatformMetrics platformMetrics;

    @BeforeEach
    void setUp() {
        prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        prometheusRegistry.config().commonTags("application", "rezonans-music-platform", "environment", "test");

        new ClassLoaderMetrics().bindTo(prometheusRegistry);
        new JvmMemoryMetrics().bindTo(prometheusRegistry);
        new JvmGcMetrics().bindTo(prometheusRegistry);
        new ProcessorMetrics().bindTo(prometheusRegistry);
        new JvmThreadMetrics().bindTo(prometheusRegistry);
        new FileDescriptorMetrics().bindTo(prometheusRegistry);
        new UptimeMetrics().bindTo(prometheusRegistry);

        platformMetrics = new MusicPlatformMetrics(prometheusRegistry);
    }

    @Test
    @DisplayName("Should increment songs played counter with genre tags and produce Prometheus scrape output")
    void recordSongsPlayed_shouldExposePrometheusMetrics() {
        platformMetrics.incrementSongsPlayed("Rock");
        platformMetrics.incrementSongsPlayed("Rock");
        platformMetrics.incrementSongsPlayed("Pop");

        String scrapeOutput = prometheusRegistry.scrape();

        assertThat(scrapeOutput)
                .contains("music_songs_streamed_total")
                .contains("genre=\"Rock\"")
                .contains("genre=\"Pop\"")
                .contains("application=\"rezonans-music-platform\"");
    }

    @Test
    @DisplayName("Should record Kafka domain events and Spark recommendations metrics")
    void recordKafkaAndRecommendations_shouldAppearInPrometheusScrape() {
        platformMetrics.incrementKafkaEvent("SongCreatedEvent");
        platformMetrics.incrementKafkaEvent("UserRegisteredEvent");
        platformMetrics.incrementRecommendations("Cassandra-Spark-ALS");
        platformMetrics.sessionStarted();

        String scrapeOutput = prometheusRegistry.scrape();

        assertThat(scrapeOutput)
                .contains("music_kafka_events_published_total")
                .contains("eventType=\"SongCreatedEvent\"")
                .contains("music_recommendations_generated_total")
                .contains("strategy=\"Cassandra-Spark-ALS\"")
                .contains("music_sessions_active");
    }

    @Test
    @DisplayName("Should record CQRS execution timer accurately")
    void recordCqrsExecution_shouldRecordLatencyTimer() throws Exception {
        String result = platformMetrics.recordCqrsExecution("GetSongByIdQuery", () -> {
            Thread.sleep(10);
            return "SUCCESS";
        });

        assertThat(result).isEqualTo("SUCCESS");

        String scrapeOutput = prometheusRegistry.scrape();
        assertThat(scrapeOutput)
                .contains("music_cqrs_execution_time_seconds")
                .contains("operation=\"GetSongByIdQuery\"");
    }

    @Test
    @DisplayName("OpenTelemetry tracer should generate valid traceId, spanId and trace context")
    void openTelemetryTracer_shouldCreateValidSpanAndTraceContext() {
        Resource resource = Resource.getDefault().merge(
                Resource.create(Attributes.of(AttributeKey.stringKey("service.name"), "rezonans-music-platform")));

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .setResource(resource)
                .build();

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();

        io.opentelemetry.api.trace.Tracer otelTracer = openTelemetry.getTracer("rezonans-music-platform");
        OtelCurrentTraceContext currentTraceContext = new OtelCurrentTraceContext();
        OtelBaggageManager baggageManager = new OtelBaggageManager(currentTraceContext, Collections.emptyList(), Collections.emptyList());
        Tracer tracer = new OtelTracer(otelTracer, currentTraceContext, event -> {}, baggageManager);

        Span span = tracer.nextSpan().name("cqrs-command-play-song").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
            assertThat(span.context().traceId()).isNotNull().isNotBlank();
            assertThat(span.context().spanId()).isNotNull().isNotBlank();
            assertThat(tracer.currentSpan()).isNotNull();
            assertThat(tracer.currentSpan().context().traceId()).isEqualTo(span.context().traceId());
        } finally {
            span.end();
        }
    }
}
