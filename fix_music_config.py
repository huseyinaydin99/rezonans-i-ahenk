with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java', 'r') as f:
    content = f.read()

content = content.replace('''    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.ofDefaults();
    }''', '')
content = content.replace('import io.github.resilience4j.retry.RetryRegistry;', '')

with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/MusicInfrastructureConfig.java', 'w') as f:
    f.write(content)
