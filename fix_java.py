import os
import re

# 1. Fix Playlist constructor and getId()
playlist_path = 'music-domain/src/main/java/tr/com/huseyinaydin/domain/playlist/Playlist.java'
if os.path.exists(playlist_path):
    with open(playlist_path, 'r') as f:
        content = f.read()
    
    # Add getters and a constructor
    if 'public UUID getId()' not in content:
        methods = """
    public Playlist(String name, UUID userId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.userId = userId;
    }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public UUID getUserId() { return userId; }
}
"""
        content = re.sub(r'}\s*$', methods, content)
        with open(playlist_path, 'w') as f:
            f.write(content)

# 2. Fix Pipeline behaviors
behaviors = ['ValidationBehavior.java', 'MembershipTierBehavior.java']
for b in behaviors:
    path = f'music-application/src/main/java/tr/com/huseyinaydin/application/pipeline/{b}'
    if os.path.exists(path):
        with open(path, 'r') as f:
            content = f.read()
        content = content.replace('implements IPipelineBehavior', 'implements IPipelineBehavior<TRequest, TResponse>')
        with open(path, 'w') as f:
            f.write(content)

# 3. Fix IPipelineBehavior signature if needed? 
# Wait, let's just check IPipelineBehavior
pipe_path = 'music-application/src/main/java/tr/com/huseyinaydin/application/cqrs/IPipelineBehavior.java'
if os.path.exists(pipe_path):
    with open(pipe_path, 'r') as f:
        content = f.read()
    if '<TRequest, TResponse>' not in content:
        content = content.replace('public interface IPipelineBehavior', 'public interface IPipelineBehavior<TRequest, TResponse>')
        content = content.replace('handle(Object request', 'handle(TRequest request')
        with open(pipe_path, 'w') as f:
            f.write(content)

# 4. Fix CreateSongCommand (Builder usage)
create_song = 'music-application/src/main/java/tr/com/huseyinaydin/application/song/commands/CreateSongCommand.java'
if os.path.exists(create_song):
    with open(create_song, 'r') as f:
        content = f.read()
    content = re.sub(r'new Song\((.*?)\);', r'Song.builder().title(request.title()).audioUrl(request.audioUrl()).artistId(request.artistId()).build();', content)
    with open(create_song, 'w') as f:
        f.write(content)

# 5. Fix remaining BusinessExceptions
import glob
for filepath in glob.iglob('music-application/**/*.java', recursive=True):
    with open(filepath, 'r') as f:
        content = f.read()
    if 'new BusinessException("' in content:
        content = re.sub(r'new BusinessException\("([^"]+)"\)', r'new BusinessException("ERR", "\1")', content)
        with open(filepath, 'w') as f:
            f.write(content)

