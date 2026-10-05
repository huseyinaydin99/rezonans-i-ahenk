import os

sec_cfg = 'music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/config/SecurityConfig.java'
if os.path.exists(sec_cfg):
    with open(sec_cfg, 'r') as f:
        content = f.read()
    
    # Let's change new JwtAuthenticationFilter(jwtService, tracer) to just use jwtService
    # Wait, the error said required: JwtTokenProvider. Let's just cast or see.
    content = content.replace('new JwtAuthenticationFilter(jwtService, tracer)', 'new JwtAuthenticationFilter((tr.com.huseyinaydin.infrastructure.security.JwtTokenProvider) jwtService)')
    with open(sec_cfg, 'w') as f:
        f.write(content)

