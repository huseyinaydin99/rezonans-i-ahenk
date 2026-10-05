import os

sec_cfg = 'music-web/src/main/java/tr/com/huseyinaydin/web/config/SecurityConfig.java'
if os.path.exists(sec_cfg):
    with open(sec_cfg, 'r') as f:
        content = f.read()
    
    # Remove constructor and field
    content = content.replace('private final JwtAuthenticationFilter jwtAuthFilter;', '')
    content = content.replace('public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {\n        this.jwtAuthFilter = jwtAuthFilter;\n    }', '')
    
    # JSF apps usually use STATEFUL session (SessionCreationPolicy.IF_REQUIRED) instead of STATELESS
    content = content.replace('SessionCreationPolicy.STATELESS', 'SessionCreationPolicy.IF_REQUIRED')
    
    with open(sec_cfg, 'w') as f:
        f.write(content)

