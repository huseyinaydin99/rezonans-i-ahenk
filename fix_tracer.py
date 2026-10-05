with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java', 'r') as f:
    content = f.read()

content = content.replace('import org.springframework.cache.annotation.EnableCaching;', '''import org.springframework.cache.annotation.EnableCaching;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.BaggageManager;
import java.util.Map;
import io.micrometer.tracing.ScopedSpan;
''')

content = content.replace('public class MusicInfrastructureConfig {', '''public class MusicInfrastructureConfig {
    @Bean
    public Tracer tracer() {
        return io.micrometer.tracing.Tracer.NOOP;
    }
''')

with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java', 'w') as f:
    f.write(content)
