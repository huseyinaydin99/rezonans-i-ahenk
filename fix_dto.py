path = 'music-application/src/main/java/tr/com/huseyinaydin/application/home/dtos/HomeDashboardDto.java'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('private String title;', 'private Long songId;\n        private String title;')
c = c.replace('public SongSnippet(String title, String artist, String coverUrl) {', 'public SongSnippet(Long songId, String title, String artist, String coverUrl) {\n            this.songId = songId;')
c = c.replace('public String getTitle() { return title; }', 'public Long getSongId() { return songId; }\n        public String getTitle() { return title; }')

with open(path, 'w') as f:
    f.write(c)

path_service = 'music-application/src/main/java/tr/com/huseyinaydin/application/home/services/HomeServiceImpl.java'
with open(path_service, 'r') as f:
    s = f.read()

# Replace new SongSnippet("...", "...", "...") with new SongSnippet(1L, "...", "...", "...")
import re
s = re.sub(r'new SongSnippet\(([^,]+),\s*([^,]+),\s*([^)]+)\)', r'new SongSnippet(1L, \1, \2, \3)', s)

with open(path_service, 'w') as f:
    f.write(s)
