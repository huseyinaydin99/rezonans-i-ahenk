with open('music-persistence/src/main/java/tr/com/huseyinaydin/persistence/config/PersistenceConfig.java', 'r') as f:
    content = f.read()

content = content.replace('@Bean\n    \n    @Bean', '@Bean')

with open('music-persistence/src/main/java/tr/com/huseyinaydin/persistence/config/PersistenceConfig.java', 'w') as f:
    f.write(content)
