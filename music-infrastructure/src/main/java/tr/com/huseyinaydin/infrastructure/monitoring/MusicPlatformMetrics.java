package tr.com.huseyinaydin.infrastructure.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Platforma özel iş (Business) metriklerini Prometheus/Grafana için kaydeden bileşen.
 */
@Component
public class MusicPlatformMetrics {

    private final Counter songsPlayedCounter;
    private final Counter membershipsUpgradedCounter;

    public MusicPlatformMetrics(MeterRegistry meterRegistry) {
        // Grafana üzerinde "Saniyede Dinlenen Şarkı" grafiğini çizebilmek için özel sayaç
        this.songsPlayedCounter = Counter.builder("music_songs_played_total")
                .description("Toplam dinlenen şarkı sayısı")
                .register(meterRegistry);

        this.membershipsUpgradedCounter = Counter.builder("music_memberships_upgraded_total")
                .description("Premium veya Elite pakete geçen toplam kullanıcı sayısı")
                .register(meterRegistry);
    }

    public void incrementSongsPlayed() {
        songsPlayedCounter.increment();
    }

    public void incrementMembershipsUpgraded() {
        membershipsUpgradedCounter.increment();
    }
}
