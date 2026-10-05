import os
import re

# 1. Add Getters to Song.java
song_path = 'music-domain/src/main/java/tr/com/huseyinaydin/domain/song/Song.java'
with open(song_path, 'r') as f:
    content = f.read()

getters = """
    public String getCoverImageUrl() { return coverImageUrl; }
    public String getAudioUrl() { return audioUrl; }
    public Duration getDuration() { return duration; }
    public MembershipTier getTier() { return tier; }
    public LocalDateTime getReleaseDate() { return releaseDate; }
    public UUID getArtistId() { return artistId; }
    public UUID getGenreId() { return genreId; }
    public long getPlayCount() { return playCount; }
    public void incrementPlayCount() { this.playCount++; }
}
"""
content = re.sub(r'}\s*$', getters, content)
with open(song_path, 'w') as f:
    f.write(content)

# 2. Add Getters to AppUser.java
user_path = 'music-domain/src/main/java/tr/com/huseyinaydin/domain/user/AppUser.java'
if os.path.exists(user_path):
    with open(user_path, 'r') as f:
        content = f.read()
    
    user_getters = """
    public MembershipTier getTier() { return tier; }
    public String getUsername() { return username; }
}
"""
    content = re.sub(r'}\s*$', user_getters, content)
    with open(user_path, 'w') as f:
        f.write(content)

# 3. Fix MembershipTier ELITE -> PREMIUM if necessary
# Wait, ELITE was missing, let's just change ELITE to PREMIUM in AudioStreamingService.java
stream_svc = 'music-application/src/main/java/tr/com/huseyinaydin/application/streaming/AudioStreamingService.java'
if os.path.exists(stream_svc):
    with open(stream_svc, 'r') as f:
        content = f.read()
    content = content.replace('MembershipTier.ELITE', 'MembershipTier.PREMIUM')
    with open(stream_svc, 'w') as f:
        f.write(content)

# 4. Fix BusinessException one-arg constructors
for handler in ['CopyrightCheckHandler.java', 'FormatValidationHandler.java', 'VirusScanHandler.java']:
    path = f'music-application/src/main/java/tr/com/huseyinaydin/application/song/upload/{handler}'
    if os.path.exists(path):
        with open(path, 'r') as f:
            content = f.read()
        content = re.sub(r'new BusinessException\("(.*?)"\)', r'new BusinessException("ERR", "\1")', content)
        with open(path, 'w') as f:
            f.write(content)

