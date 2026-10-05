import os

init = 'music-web/src/main/java/tr/com/huseyinaydin/web/config/WebAppInitializer.java'
if os.path.exists(init):
    with open(init, 'r') as f:
        content = f.read()
    
    # Replace the old security filter registration
    old_sec = 'FilterRegistration.Dynamic security = ctx.addFilter("springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain"));'
    new_sec = """DelegatingFilterProxy securityProxy = new DelegatingFilterProxy("springSecurityFilterChain");
        securityProxy.setContextAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.apiServlet");
        FilterRegistration.Dynamic security = ctx.addFilter("springSecurityFilterChain", securityProxy);"""
    
    content = content.replace(old_sec, new_sec)
    
    with open(init, 'w') as f:
        f.write(content)

