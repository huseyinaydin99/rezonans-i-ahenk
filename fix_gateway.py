with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java', 'r') as f:
    content = f.read()

content = content.replace('import org.springframework.cache.annotation.EnableCaching;', '''import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ConfigurableApplicationContext;
''')

content = content.replace('public class MusicInfrastructureConfig {', '''public class MusicInfrastructureConfig {
    @Bean
    public RouteLocatorBuilder routeLocatorBuilder(ConfigurableApplicationContext context) {
        return new RouteLocatorBuilder(context);
    }
''')

with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java', 'w') as f:
    f.write(content)
