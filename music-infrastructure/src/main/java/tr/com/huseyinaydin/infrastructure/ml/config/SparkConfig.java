package tr.com.huseyinaydin.infrastructure.ml.config;

import org.springframework.context.annotation.Configuration;

/**
 * Büyük Veri (Big Data) işleme motoru Apache Spark yapılandırması.
 * application.properties üzerinden master url ve uygulama adı alınarak
 * SparkSession nesnesi (Spark 3.x) Spring IoC'ye eklenir.
 */
@Configuration
public class SparkConfig {
    public static final String SPARK_APP_NAME = "MusicRecommendationEngine";
    public static final String SPARK_MASTER = "spark://localhost:7077";
}
