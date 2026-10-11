package tr.com.huseyinaydin.domain.user;

import tr.com.huseyinaydin.domain.common.Entity;

import java.util.UUID;

public class OperationClaim extends Entity<UUID> {
    private String name;

    protected OperationClaim() {
        super();
    }

    public OperationClaim(String name) {
        super();
        this.id = UUID.randomUUID();
        this.name = name;
        this.createdDate = java.time.LocalDateTime.now();
        this.version = 0L;
    }

    public String getName() { return name; }
}
