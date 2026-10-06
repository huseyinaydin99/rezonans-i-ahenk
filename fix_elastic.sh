sed -i '' '/@EnableCaching/a\
@org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories(basePackages = "tr.com.huseyinaydin.infrastructure")
' music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java
