import os
import re

# 1. MembershipTierFactory
tier_factory = 'music-application/src/main/java/tr/com/huseyinaydin/application/membership/rules/MembershipTierFactory.java'
if os.path.exists(tier_factory):
    with open(tier_factory, 'r') as f:
        content = f.read()
    content = content.replace('case ELITE', 'case ELIT')
    with open(tier_factory, 'w') as f:
        f.write(content)

# 2. BehaviorConfig.java - remove @Override from NoOpCurrentUserService
beh_cfg = 'music-application/src/main/java/tr/com/huseyinaydin/application/config/BehaviorConfig.java'
if os.path.exists(beh_cfg):
    with open(beh_cfg, 'r') as f:
        content = f.read()
    
    # We will remove the @Override annotations inside NoOpCurrentUserService
    class_start = content.find('static class NoOpCurrentUserService')
    if class_start != -1:
        prefix = content[:class_start]
        suffix = content[class_start:]
        suffix = suffix.replace('@Override', '')
        content = prefix + suffix
        with open(beh_cfg, 'w') as f:
            f.write(content)

# 3. CreateArtistCommand.java
create_artist = 'music-application/src/main/java/tr/com/huseyinaydin/application/artist/commands/CreateArtistCommand.java'
if os.path.exists(create_artist):
    with open(create_artist, 'r') as f:
        content = f.read()
    
    builder_code = """Artist.builder()
                    .name(command.name())
                    .biography(command.biography())
                    .imageUrl(command.imageUrl())
                    .build()"""
    
    content = re.sub(r'new Artist\([^;]+\)', builder_code, content)
    with open(create_artist, 'w') as f:
        f.write(content)

