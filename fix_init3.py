import os

init = 'music-web/src/main/java/tr/com/huseyinaydin/web/config/WebAppInitializer.java'
if os.path.exists(init):
    with open(init, 'r') as f:
        content = f.read()
    
    # Add SecurityConfig.class to rootCtx
    content = content.replace('tr.com.huseyinaydin.persistence.config.PersistenceConfig.class', 'tr.com.huseyinaydin.persistence.config.PersistenceConfig.class,\n                tr.com.huseyinaydin.web.config.SecurityConfig.class')
    
    # Restore DelegatingFilterProxy to NOT use apiServlet context, so it uses rootCtx
    old_proxy = """DelegatingFilterProxy securityProxy = new DelegatingFilterProxy("springSecurityFilterChain");
        securityProxy.setContextAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.apiServlet");
        FilterRegistration.Dynamic security = ctx.addFilter("springSecurityFilterChain", securityProxy);"""
    
    new_proxy = 'FilterRegistration.Dynamic security = ctx.addFilter("springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain"));'
    
    content = content.replace(old_proxy, new_proxy)
    
    with open(init, 'w') as f:
        f.write(content)

