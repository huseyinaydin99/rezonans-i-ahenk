with open('music-persistence/src/main/java/tr/com/huseyinaydin/persistence/config/PersistenceConfig.java', 'r') as f:
    content = f.read()

content = content.replace('public class PersistenceConfig {', '''@org.springframework.transaction.annotation.EnableTransactionManagement
public class PersistenceConfig {
''')

with open('music-persistence/src/main/java/tr/com/huseyinaydin/persistence/config/PersistenceConfig.java', 'w') as f:
    f.write(content)
