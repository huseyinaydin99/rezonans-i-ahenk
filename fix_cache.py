with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java', 'r') as f:
    content = f.read()

content = content.replace('import org.springframework.context.annotation.Configuration;', 'import org.springframework.context.annotation.Configuration;\nimport org.springframework.cache.CacheManager;\nimport org.springframework.cache.concurrent.ConcurrentMapCacheManager;\nimport org.springframework.cache.annotation.EnableCaching;')

content = content.replace('@Configuration', '@Configuration\n@EnableCaching')

content = content.replace('public class MusicInfrastructureConfig {', '''public class MusicInfrastructureConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("queryCache", "referenceDataCache");
    }
''')

with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java', 'w') as f:
    f.write(content)
