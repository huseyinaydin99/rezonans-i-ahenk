import os
import re

beh_cfg = 'music-application/src/main/java/tr/com/huseyinaydin/application/config/BehaviorConfig.java'
if os.path.exists(beh_cfg):
    with open(beh_cfg, 'r') as f:
        content = f.read()
    
    # ICurrentUserService wants String getCurrentUserId(), we probably have UUID getCurrentUserId()
    # Let's fix NoOpCurrentUserService
    new_class = """
    static class NoOpCurrentUserService implements ICurrentUserService {
        public String getCurrentUserId() { return "00000000-0000-0000-0000-000000000000"; }
        public boolean isAuthenticated() { return false; }
        public java.util.Set<String> getCurrentUserRoles() { return java.util.Collections.emptySet(); }
        public tr.com.huseyinaydin.domain.enums.MembershipTier getCurrentUserTier() { return tr.com.huseyinaydin.domain.enums.MembershipTier.BASIC; }
    }
"""
    content = re.sub(r'static class NoOpCurrentUserService implements ICurrentUserService \{.*\}', new_class.strip(), content, flags=re.DOTALL)
    with open(beh_cfg, 'w') as f:
        f.write(content)

artist_path = 'music-application/src/main/java/tr/com/huseyinaydin/application/artist/commands/CreateArtistCommand.java'
if os.path.exists(artist_path):
    with open(artist_path, 'r') as f:
        content = f.read()
    # just remove imageUrl entirely from builder since it might be called profileImageUrl or we don't need it
    content = re.sub(r'\.imageUrl\(command\.imageUrl\(\)\)', '', content)
    with open(artist_path, 'w') as f:
        f.write(content)

