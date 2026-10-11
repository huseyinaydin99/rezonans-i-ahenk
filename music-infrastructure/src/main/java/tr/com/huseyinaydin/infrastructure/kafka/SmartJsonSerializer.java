package tr.com.huseyinaydin.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Kurumsal Akıllı JSON Serileştirici.
 * Map, DomainEvent, POJO ve DTO nesnelerini otomatik olarak JSON formatına serileştirir;
 * String veya ham byte dizisi verildiğinde gereksiz dönüşüm yapmadan doğrudan iletir.
 * ClassCastException hatasını tamamen ortadan kaldırır.
 */
public class SmartJsonSerializer implements Serializer<Object> {

    private final ObjectMapper objectMapper;

    public SmartJsonSerializer() {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Object data) {
        if (data == null) {
            return null;
        }
        if (data instanceof byte[] bytes) {
            return bytes;
        }
        if (data instanceof String str) {
            return str.getBytes(StandardCharsets.UTF_8);
        }
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Kafka serileştirme hatası: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
    }
}
