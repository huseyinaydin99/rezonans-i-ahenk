package tr.com.huseyinaydin.domain.user;

import tr.com.huseyinaydin.domain.common.Entity;

import java.util.UUID;

public class UserOperationClaim extends Entity<UUID> {
    private UUID userId;
    private UUID operationClaimId;

    protected UserOperationClaim() {
        super();
    }

    public UserOperationClaim(UUID userId, UUID operationClaimId) {
        super();
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.operationClaimId = operationClaimId;
        this.createdDate = java.time.LocalDateTime.now();
        this.version = 0L;
    }

    public UUID getUserId() { return userId; }

    public UUID getOperationClaimId() { return operationClaimId; }
}
