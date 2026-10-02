package tr.com.huseyinaydin.infrastructure.storage.config;

import org.springframework.context.annotation.Configuration;

/**
 * Amazon S3 mimarisiyle %100 uyumlu yerel (On-Premise) Nesne Depolama 
 * (Object Storage) aracı olan MinIO'nun yapılandırma ayarları (Mocked Config).
 */
@Configuration
public class MinioConfig {
    // Gerçek senaryoda MinioClient bean'i burada yaratılıp Spring IoC'ye eklenir.
    public static final String AUDIO_BUCKET = "music-audio-bucket";
    public static final String IMAGE_BUCKET = "music-image-bucket";
}
