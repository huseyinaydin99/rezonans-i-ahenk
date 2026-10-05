with open('music-web/src/main/java/tr/com/huseyinaydin/web/config/SecurityConfig.java', 'r') as f:
    content = f.read()

content = content.replace('import tr.com.huseyinaydin.application.ports.IJwtService;', '''import tr.com.huseyinaydin.infrastructure.security.JwtTokenProvider;
''')

content = content.replace('private final IJwtService jwtService;', '''private final JwtTokenProvider jwtTokenProvider;
''')

content = content.replace('public SecurityConfig(IJwtService jwtService) {', '''public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
''')

content = content.replace('this.jwtService = jwtService;', '''this.jwtTokenProvider = jwtTokenProvider;
''')

content = content.replace('new JwtAuthenticationFilter((tr.com.huseyinaydin.infrastructure.security.JwtTokenProvider) jwtService),', '''new JwtAuthenticationFilter(jwtTokenProvider),
''')

with open('music-web/src/main/java/tr/com/huseyinaydin/web/config/SecurityConfig.java', 'w') as f:
    f.write(content)
