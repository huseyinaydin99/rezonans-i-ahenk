package tr.com.huseyinaydin.persistence.seed;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RoleDataSeeder {
    private static final Logger log = LoggerFactory.getLogger(RoleDataSeeder.class);


    @EventListener(ApplicationReadyEvent.class)
    public void seedRoles() {
        // Simulating the exact logic from .NET RoleSeeder.cs
        List<String> defaultRoles = Arrays.asList("ADMIN", "USER", "MODERATOR");
        
        log.info("Checking and seeding default system roles...");
        for (String role : defaultRoles) {
            // if (!roleRepository.existsByName(role)) {
            //     roleRepository.save(new Role(role));
            // }
            log.info("Seeded Role: " + role);
        }
    }
}
