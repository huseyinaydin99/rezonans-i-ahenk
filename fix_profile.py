import os

file_path = 'music-persistence/src/main/java/tr/com/huseyinaydin/persistence/config/PersistenceConfig.java'
with open(file_path, 'r') as f:
    content = f.read()

content = content.replace('@Profile({"dev", "test"})', '@Profile({"dev", "test", "default"})')

with open(file_path, 'w') as f:
    f.write(content)

