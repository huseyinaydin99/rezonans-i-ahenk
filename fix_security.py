path = 'music-web/src/main/java/tr/com/huseyinaydin/web/config/SecurityConfig.java'
with open(path, 'r') as f:
    c = f.read()

target = 'new AntPathRequestMatcher("/api/v1/auth/**"), new AntPathRequestMatcher("/health")'
replacement = 'new AntPathRequestMatcher("/api/v1/auth/**"), new AntPathRequestMatcher("/health"), new AntPathRequestMatcher("/api/Song/Play/**")'

c = c.replace(target, replacement)

with open(path, 'w') as f:
    f.write(c)
