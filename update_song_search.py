path = 'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/SongController.java'
with open(path, 'r') as f:
    c = f.read()

search_method = '''
    @GetMapping(value = {"/Song/Search", "/api/Song/Search"})
    public ResponseEntity<java.util.List<Map<String, Object>>> search(@RequestParam(value="q", required=false) String q) {
        java.util.List<Map<String, Object>> allSongs = new java.util.ArrayList<>();
        
        allSongs.add(createSong(1L, "Blinding Lights", "The Weeknd", "https://images.unsplash.com/photo-1614680376593-902f74cf0d41?w=300", "Gold"));
        allSongs.add(createSong(2L, "Starboy", "The Weeknd", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300", "Basic"));
        allSongs.add(createSong(3L, "Shape of You", "Ed Sheeran", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300", "Premium"));
        allSongs.add(createSong(4L, "Perfect", "Ed Sheeran", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300", "Premium"));
        allSongs.add(createSong(5L, "Bohemian Rhapsody", "Queen", "https://images.unsplash.com/photo-1493225457124-a1a2a53b51ee?w=300", "Elit"));
        allSongs.add(createSong(6L, "Dance Monkey", "Tones And I", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300", "Basic"));
        allSongs.add(createSong(7L, "Watermelon Sugar", "Harry Styles", "https://images.unsplash.com/photo-1493225457124-a1a2a53b51ee?w=300", "Gold"));

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(allSongs);
        }

        String query = q.toLowerCase();
        java.util.List<Map<String, Object>> filtered = new java.util.ArrayList<>();
        for (Map<String, Object> song : allSongs) {
            String title = song.get("title").toString().toLowerCase();
            String artist = song.get("artist").toString().toLowerCase();
            if (title.contains(query) || artist.contains(query)) {
                filtered.add(song);
            }
        }
        
        return ResponseEntity.ok(filtered);
    }

    private Map<String, Object> createSong(Long id, String title, String artist, String cover, String tier) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("artist", artist);
        map.put("cover", cover);
        map.put("tier", tier);
        return map;
    }
'''

import re
c = re.sub(r'public class SongController \{', 'public class SongController {\n' + search_method, c)
c = c.replace('import org.springframework.web.bind.annotation.RequestParam;', '')
c = c.replace('import org.springframework.web.bind.annotation.RestController;', 'import org.springframework.web.bind.annotation.RestController;\nimport org.springframework.web.bind.annotation.RequestParam;')

with open(path, 'w') as f:
    f.write(c)
