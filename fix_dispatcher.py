with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/events/SpringDomainEventDispatcher.java', 'r') as f:
    content = f.read()

content = content.replace('@Component', '@Component\n@org.springframework.context.annotation.Primary')

with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/events/SpringDomainEventDispatcher.java', 'w') as f:
    f.write(content)
