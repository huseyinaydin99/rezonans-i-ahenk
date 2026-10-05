import os

file_path = 'music-persistence/src/main/java/tr/com/huseyinaydin/persistence/config/PersistenceConfig.java'
with open(file_path, 'r') as f:
    content = f.read()

# Make sure devDataSource is ALWAYS created by removing its @Profile
import re
content = re.sub(r'@Profile\(\{[^\}]+\}\)\s+public DataSource devDataSource', 'public DataSource devDataSource', content)
content = re.sub(r'@Profile\("[^"]+"\)\s+public DataSource devDataSource', 'public DataSource devDataSource', content)

# Also fix the literal string issue by providing defaults in the @Value annotations
content = content.replace('@Value("${spring.datasource.url}")', '@Value("${spring.datasource.url:jdbc:h2:mem:musicdb;DB_CLOSE_DELAY=-1}")')
content = content.replace('@Value("${spring.datasource.username}")', '@Value("${spring.datasource.username:sa}")')
content = content.replace('@Value("${spring.datasource.password}")', '@Value("${spring.datasource.password:password}")')

with open(file_path, 'w') as f:
    f.write(content)
