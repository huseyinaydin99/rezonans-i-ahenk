with open('music-domain/src/main/java/tr/com/huseyinaydin/domain/playlist/Playlist.java', 'r') as f:
    content = f.read()

content = content.replace('this.userId = userId;', 'this.ownerId = userId;')
content = content.replace('public UUID getUserId() { return userId; }', 'public UUID getUserId() { return ownerId; }')
content = content.replace('public Playlist(String name, UUID userId)', 'public Playlist(String name, UUID userId)')

with open('music-domain/src/main/java/tr/com/huseyinaydin/domain/playlist/Playlist.java', 'w') as f:
    f.write(content)
