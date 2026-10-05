with open('music-web/src/main/java/tr/com/huseyinaydin/web/config/SecurityConfig.java', 'r') as f:
    content = f.read()

import re

# Add import
if 'import org.springframework.security.web.util.matcher.AntPathRequestMatcher;' not in content:
    content = content.replace('import org.springframework.context.annotation.Configuration;', 'import org.springframework.context.annotation.Configuration;\nimport org.springframework.security.web.util.matcher.AntPathRequestMatcher;')

# Fix apiFilterChain
content = content.replace('.securityMatcher("/api/**")', '.securityMatcher(new AntPathRequestMatcher("/api/**"))')
content = content.replace('.requestMatchers("/api/v1/auth/**", "/health")', '.requestMatchers(new AntPathRequestMatcher("/api/v1/auth/**"), new AntPathRequestMatcher("/health"))')

# Fix webFilterChain
content = content.replace('.requestMatchers("/login.xhtml", "/register.xhtml", "/error/**", "/resources/**", "/jakarta.faces.resource/**")', '.requestMatchers(new AntPathRequestMatcher("/login.xhtml"), new AntPathRequestMatcher("/register.xhtml"), new AntPathRequestMatcher("/error/**"), new AntPathRequestMatcher("/resources/**"), new AntPathRequestMatcher("/jakarta.faces.resource/**"))')

with open('music-web/src/main/java/tr/com/huseyinaydin/web/config/SecurityConfig.java', 'w') as f:
    f.write(content)
