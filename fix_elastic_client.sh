sed -i '' 's/public class MusicInfrastructureConfig/public class MusicInfrastructureConfig extends org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration/' music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java
sed -i '' '/public class MusicInfrastructureConfig/a\
    @Override\
    public org.springframework.data.elasticsearch.client.ClientConfiguration clientConfiguration() {\
        return org.springframework.data.elasticsearch.client.ClientConfiguration.builder()\
                .connectedTo("localhost:9200")\
                .build();\
    }
' music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java
