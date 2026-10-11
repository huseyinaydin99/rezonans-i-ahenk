package tr.com.huseyinaydin.infrastructure.cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Cassandra kümesinin erişilebilirliğini kontrol eden kurumsal Condition sınıfı.
 * Cassandra portu (9042) aktif ve CQL protokolüne yanıt veriyorsa CassandraConfig devreye girer.
 * Eğer Cassandra konteyneri kapalıysa veya bağlantı sıfırlanıyorsa, uygulamanın çökmesini
 * engelleyerek Spring ApplicationContext'inin kesintisiz şekilde ayağa kalkmasını sağlar.
 */
public class CassandraAvailableCondition implements Condition {

    private static final Logger log = LoggerFactory.getLogger(CassandraAvailableCondition.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String enabled = context.getEnvironment().getProperty("spring.cassandra.enabled", "true");
        if ("false".equalsIgnoreCase(enabled)) {
            log.info("ℹ️ [CASSANDRA] 'spring.cassandra.enabled=false' konfigürasyonu nedeniyle Cassandra devre dışı bırakıldı.");
            return false;
        }

        String host = context.getEnvironment().getProperty("spring.cassandra.contact-points", "localhost");
        int port = 9042;
        String portStr = context.getEnvironment().getProperty("spring.cassandra.port");
        if (portStr != null && !portStr.isBlank()) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException ignored) {}
        }

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1000);
            socket.setSoTimeout(1000);
            // CQL v4 OPTIONS frame ping (0x04 = v4 request, stream=1, opcode=0x05 OPTIONS, length=0)
            socket.getOutputStream().write(new byte[]{0x04, 0x00, 0x00, 0x01, 0x05, 0x00, 0x00, 0x00, 0x00});
            socket.getOutputStream().flush();
            int b = socket.getInputStream().read();
            if (b == -1) {
                log.warn("⚠️ [CASSANDRA] Düğüm ({}:{}) kanal bağlantısını kapattı. Cassandra devre dışı bırakılıyor.", host, port);
                return false;
            }
            log.info("✅ [CASSANDRA] Düğüm ({}:{}) aktif ve CQL protokolüne yanıt verdi. Cassandra bileşenleri başlatılıyor.", host, port);
            return true;
        } catch (Exception e) {
            log.warn("⚠️ [CASSANDRA ÇEVRİMDIŞI] Düğüm ({}:{}) erişilemiyor ({}). Uygulamanın kesintisiz hizmet vermesi için Cassandra bileşenleri güvenle devre dışı bırakıldı (Fallback kataloğu devrede).",
                    host, port, e.getMessage());
            return false;
        }
    }
}
