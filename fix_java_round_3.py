import os
import re

# 1. Fix Behaviors
behaviors = ['ValidationBehavior.java', 'MembershipTierBehavior.java', 'CachingBehavior.java', 'LoggingBehavior.java']
for b in behaviors:
    path = f'music-application/src/main/java/tr/com/huseyinaydin/application/pipeline/{b}'
    if os.path.exists(path):
        with open(path, 'r') as f:
            content = f.read()
        
        # Class generic signature (if missed earlier)
        if 'class CachingBehavior implements' in content:
            content = content.replace('class CachingBehavior implements', 'class CachingBehavior<TRequest, TResponse> implements')
        if 'class LoggingBehavior implements' in content:
            content = content.replace('class LoggingBehavior implements', 'class LoggingBehavior<TRequest, TResponse> implements')
            
        # Method generic shadow
        content = content.replace('<TRequest, TResponse> TResponse handle', 'TResponse handle')
        content = content.replace('next.handle()', 'next.proceed()')
        
        # BusinessException fixes
        content = re.sub(r'new BusinessException\("([^",]+)"\)', r'new BusinessException("ERR", "\1")', content)
        
        with open(path, 'w') as f:
            f.write(content)

# 2. Fix CreateSongCommand
create_song = 'music-application/src/main/java/tr/com/huseyinaydin/application/song/commands/CreateSongCommand.java'
if os.path.exists(create_song):
    with open(create_song, 'r') as f:
        content = f.read()
    
    # Replace new Song(...) with Song.builder()
    builder_code = """Song.builder()
                    .title(command.title())
                    .coverImageUrl(command.coverImageUrl())
                    .audioUrl(command.audioUrl())
                    .duration(Duration.ofSeconds(command.durationSeconds()))
                    .tier(MembershipTier.valueOf(command.tierName().toUpperCase()))
                    .releaseDate(command.releaseDate())
                    .artistId(command.artistId())
                    .genreId(command.genreId())
                    .build();"""
    
    content = re.sub(r'new Song\([^;]+;', builder_code, content)
    with open(create_song, 'w') as f:
        f.write(content)

