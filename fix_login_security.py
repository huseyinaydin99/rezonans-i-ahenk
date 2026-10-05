path = 'music-web/src/main/java/tr/com/huseyinaydin/web/beans/LoginBean.java'
with open(path, 'r') as f:
    c = f.read()

imports = '''import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;'''

if 'org.springframework.security' not in c:
    c = c.replace('import java.io.IOException;', 'import java.io.IOException;\n' + imports)

target = '''            this.isLoggedIn = true;
            this.loggedInUserRole = tokenResponse.getUserRole();
            
            this.success = true;'''

replacement = '''            this.isLoggedIn = true;
            this.loggedInUserRole = tokenResponse.getUserRole();
            
            // Spring Security'ye login olduğunu haber ver!
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(tokenResponse.getUserRole()));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(req.getEmail(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            
            HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            
            this.success = true;'''

c = c.replace(target, replacement)

# Reset success flag when getters are called or on init?
# Let's add an action to reset it, or just let Spring Security allow them to index.xhtml so they don't bounce.
# But if they manually visit login.xhtml later, it will bounce them back.
# We should reset success inside getSuccess!

c = c.replace('public boolean isSuccess() { return success; }', '''public boolean isSuccess() { 
        boolean current = success; 
        success = false; // Auto reset 
        return current; 
    }''')

with open(path, 'w') as f:
    f.write(c)

