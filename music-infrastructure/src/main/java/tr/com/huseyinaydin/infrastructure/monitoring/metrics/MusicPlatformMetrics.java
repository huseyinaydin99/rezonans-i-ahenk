package tr.com.huseyinaydin.infrastructure.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Merkezi Prometheus/Micrometer metrik yönetim servisi.
 * CQRS, Kafka, Spark AI önerileri ve müzik akışı için özel işletme metriklerini toplar.
 */
@Component
public class MusicPlatformMetrics {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeListeningSessions = new AtomicInteger(0);

    public MusicPlatformMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        meterRegistry.gauge("music.sessions.active", activeListeningSessions);
    }

    public void incrementSongsPlayed(String genre) {
        Counter.builder("music.songs.streamed.total")
                .description("Toplam çalınan/dinlenen şarkı sayısı")
                .tag("genre", genre != null ? genre : "Unknown")
                .register(meterRegistry)
                .increment();
    }

    public void incrementUserRegistered() {
        Counter.builder("music.users.registered.total")
                .description("Sisteme kaydolan toplam kullanıcı sayısı")
                .register(meterRegistry)
                .increment();
    }

    public void incrementKafkaEvent(String eventType) {
        Counter.builder("music.kafka.events.published.total")
                .description("Kafka broker'a iletilen toplam domain event sayısı")
                .tag("eventType", eventType != null ? eventType : "Unknown")
                .register(meterRegistry)
                .increment();
    }

    public void incrementRecommendations(String strategy) {
        Counter.builder("music.recommendations.generated.total")
                .description("Üretilen yapay zeka (Spark ALS / Cassandra) öneri sayısı")
                .tag("strategy", strategy != null ? strategy : "Default")
                .register(meterRegistry)
                .increment();
    }

    public <T> T recordCqrsExecution(String name, Callable<T> task) throws Exception {
        Timer timer = Timer.builder("music.cqrs.execution.time")
                .description("CQRS komut ve sorgularının yürütülme süresi")
                .tag("operation", name != null ? name : "Unknown")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        return timer.recordCallable(task);
    }

    public void recordCqrsExecution(String name, Runnable task) {
        Timer timer = Timer.builder("music.cqrs.execution.time")
                .description("CQRS komut ve sorgularının yürütülme süresi")
                .tag("operation", name != null ? name : "Unknown")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
        timer.record(task);
    }

    public void sessionStarted() {
        activeListeningSessions.incrementAndGet();
    }

    public void sessionEnded() {
        if (activeListeningSessions.get() > 0) {
            activeListeningSessions.decrementAndGet();
        }
    }

    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }
}
