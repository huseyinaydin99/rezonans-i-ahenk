with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/MusicApplicationConfig.java', 'r') as f:
    content = f.read()

content = content.replace('@Bean\n    public Mediator mediator', '''@Bean\n    @org.springframework.context.annotation.Primary\n    public Mediator mediator''')

with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/MusicApplicationConfig.java', 'w') as f:
    f.write(content)
