with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java', 'r') as f:
    content = f.read()

content = content.replace('import org.springframework.cache.annotation.EnableCaching;', 'import org.springframework.cache.annotation.EnableCaching;\nimport com.fasterxml.jackson.databind.ObjectMapper;\nimport com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;\nimport com.fasterxml.jackson.databind.SerializationFeature;')

content = content.replace('public class MusicInfrastructureConfig {', '''public class MusicInfrastructureConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
''')

with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java', 'w') as f:
    f.write(content)
