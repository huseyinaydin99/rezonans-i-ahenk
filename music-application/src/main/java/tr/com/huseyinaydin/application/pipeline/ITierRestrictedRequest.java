package tr.com.huseyinaydin.application.pipeline;

import tr.com.huseyinaydin.domain.enums.MembershipTier;

public interface ITierRestrictedRequest {
    MembershipTier getRequiredTier();
}
