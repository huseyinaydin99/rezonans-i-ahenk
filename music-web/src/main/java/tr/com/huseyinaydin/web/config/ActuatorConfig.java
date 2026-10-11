package tr.com.huseyinaydin.web.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Enterprise Actuator & Prometheus Metrik Konfigürasyonu.
 * JVM, bellek, CPU ve işletim sistemi metriklerini otomatik toplayarak
 * Prometheus ve Grafana scraper'larına hazır hale getirir.
 */
@Configuration
public class ActuatorConfig {

    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        registry.config().commonTags("application", "rezonans-music-platform", "environment", "production");

        // JVM & Sistem Metrik Binders
        new ClassLoaderMetrics().bindTo(registry);
        new JvmMemoryMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);
        new UptimeMetrics().bindTo(registry);

        return registry;
    }

    @Bean
    @Primary
    public MeterRegistry meterRegistry(PrometheusMeterRegistry prometheusMeterRegistry) {
        return prometheusMeterRegistry;
    }

    @Bean
    public MetricsEndpoint metricsEndpoint(MeterRegistry meterRegistry) {
        return new MetricsEndpoint(meterRegistry);
    }
}