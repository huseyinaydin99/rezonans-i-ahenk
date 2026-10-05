with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/BehaviorConfig.java', 'r') as f:
    content = f.read()

if not content.strip().endswith('}'):
    content += '\n}\n'
    with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/BehaviorConfig.java', 'w') as f:
        f.write(content)
