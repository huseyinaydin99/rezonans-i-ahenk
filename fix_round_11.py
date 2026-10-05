with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/BehaviorConfig.java', 'r') as f:
    content = f.read()

# Let's completely recreate the NoOpCurrentUserService
no_op_class = """
    static class NoOpCurrentUserService implements ICurrentUserService {
        public String getCurrentUserId() { return "00000000-0000-0000-0000-000000000000"; }
        public boolean isAuthenticated() { return false; }
        public java.util.Set<String> getCurrentUserRoles() { return java.util.Collections.emptySet(); }
        public tr.com.huseyinaydin.domain.enums.MembershipTier getCurrentUserTier() { return tr.com.huseyinaydin.domain.enums.MembershipTier.BASIC; }
    }
}
"""

import re
content = re.sub(r'static class NoOpCurrentUserService implements ICurrentUserService \{.*$', no_op_class.strip(), content, flags=re.DOTALL)

with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/BehaviorConfig.java', 'w') as f:
    f.write(content)
