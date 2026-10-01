package tr.com.huseyinaydin.application.pipeline;

import java.util.Set;
import tr.com.huseyinaydin.domain.enums.MembershipTier;

// Overwriting the existing one to add getCurrentUserTier() for architecture completion
public interface ICurrentUserService {
    String getCurrentUserId();
    boolean isAuthenticated();
    Set<String> getCurrentUserRoles();
    MembershipTier getCurrentUserTier(); // <-- Yeni eklenen metod
}
