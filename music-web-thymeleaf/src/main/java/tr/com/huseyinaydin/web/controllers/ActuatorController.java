package tr.com.huseyinaydin.web.controllers;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/actuator")
@Tag(name = "Actuator", description = "Sistem sağlık, bilgi, Prometheus ve izleme endpoint'leri")
public class ActuatorController {

    private static final List<String> STATUS_ORDER =
            Arrays.asList("DOWN", "OUT_OF_SERVICE", "DEGRADED", "UNKNOWN", "UP");

    private final Map<String, HealthIndicator> healthIndicators;
    private final List<InfoContributor> infoContributors;
    private final MetricsEndpoint metricsEndpoint;
    private final PrometheusMeterRegistry prometheusMeterRegistry;

    public ActuatorController(Map<String, HealthIndicator> healthIndicators,
                               List<InfoContributor> infoContributors,
                               MetricsEndpoint metricsEndpoint,
                               PrometheusMeterRegistry prometheusMeterRegistry) {
        this.healthIndicators = healthIndicators;
        this.infoContributors = infoContributors;
        this.metricsEndpoint = metricsEndpoint;
        this.prometheusMeterRegistry = prometheusMeterRegistry;
    }

    @GetMapping(value = "/prometheus", produces = "text/plain; version=0.0.4; charset=utf-8")
    @Operation(
        summary = "Prometheus Scrape Endpoint",
        description = "Prometheus ve Grafana için OpenMetrics exposition formatında metrik döner."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Prometheus metrikleri başarıyla üretildi")
    })
    public ResponseEntity<String> prometheus() {
        return ResponseEntity.ok(prometheusMeterRegistry.scrape());
    }

    @GetMapping("/health")
    @Operation(
        summary  = "Sistem sağlık durumu",
        description = "Tüm HealthIndicator'ları çalıştırır. DOWN → HTTP 503, DEGRADED → 200."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "UP veya DEGRADED"),
        @ApiResponse(responseCode = "503", description = "DOWN — sistem kullanılamaz")
    })
    public ResponseEntity<Map<String, Object>> health() {

        Map<String, Object> components = new LinkedHashMap<>();
        Status overallStatus = Status.UP;

        for (Map.Entry<String, HealthIndicator> entry : healthIndicators.entrySet()) {
            Health health = entry.getValue().health();
            Status componentStatus = health.getStatus();

            Map<String, Object> componentDetail = new LinkedHashMap<>();
            componentDetail.put("status", componentStatus.getCode());
            if (!health.getDetails().isEmpty()) {
                componentDetail.put("details", health.getDetails());
            }
            components.put(entry.getKey(), componentDetail);

            overallStatus = worse(overallStatus, componentStatus);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", overallStatus.getCode());
        response.put("components", components);

        int httpStatus = Status.DOWN.getCode().equals(overallStatus.getCode()) ? 503 : 200;
        return ResponseEntity.status(httpStatus).body(response);
    }

    @GetMapping("/info")
    @Operation(
        summary = "Uygulama bilgileri",
        description = "Tüm InfoContributor bean'lerinden derlenen uygulama meta verisi."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Uygulama bilgileri")
    })
    public ResponseEntity<Map<String, Object>> info() {
        Info.Builder builder = new Info.Builder();
        infoContributors.forEach(contributor -> contributor.contribute(builder));
        return ResponseEntity.ok(builder.build().getDetails());
    }

    @GetMapping("/metrics")
    @Operation(
        summary = "Tüm metrik isimleri",
        description = "MeterRegistry'de kayıtlı tüm meter adlarını listeler."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Metrik isim listesi")
    })
    public ResponseEntity<Map<String, Object>> metrics() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("names", metricsEndpoint.listNames().getNames());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metrics/{name}")
    @Operation(
        summary = "Belirli bir metrik",
        description = "Meter adına göre ölçüm değerlerini ve etiketlerini döner."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Metrik ayrıntıları"),
        @ApiResponse(responseCode = "404", description = "Metrik bulunamadı")
    })
    public ResponseEntity<?> metricByName(@PathVariable String name) {
        MetricsEndpoint.MetricDescriptor metric = metricsEndpoint.metric(name, null);
        if (metric == null) {
            Map<String, String> notFound = Map.of("error", "Metrik bulunamadı: " + name);
            return ResponseEntity.status(404).body(notFound);
        }
        return ResponseEntity.ok(metric);
    }

    private Status worse(Status current, Status candidate) {
        int currentIdx  = STATUS_ORDER.indexOf(current.getCode());
        int candidateIdx = STATUS_ORDER.indexOf(candidate.getCode());

        if (currentIdx  == -1) currentIdx  = STATUS_ORDER.size();
        if (candidateIdx == -1) candidateIdx = STATUS_ORDER.size();

        return candidateIdx < currentIdx ? candidate : current;
    }
}
