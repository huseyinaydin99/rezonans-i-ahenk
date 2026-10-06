path = 'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/SongController.java'
with open(path, 'r') as f:
    c = f.read()

replacement = '''
    @GetMapping(value = {"/Song/Play/{id}", "/api/Song/Play/{id}"})
    public ResponseEntity<Map<String, Object>> playSong(@PathVariable("id") Long id, jakarta.servlet.http.HttpSession session) {
        Object user = session.getAttribute("LOGGED_IN_USER");
        if (user == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Müzik dinlemek için giriş yapmalısınız.");
            return ResponseEntity.status(401).body(err);
        }

        String tier = (String) session.getAttribute("USER_TIER");
        if (tier == null) tier = "Basic";

        // Örnek Kademe Zorunlulukları (Sadece demo için: 3 ve 4 id'li şarkılar Premium ister)
        int requiredLevel = 0;
        if (id == 3 || id == 4) requiredLevel = 2; // Premium
        if (id == 5) requiredLevel = 3; // Elit

        int userLevel = 0;
        if (tier.equals("Gold")) userLevel = 1;
        if (tier.equals("Premium")) userLevel = 2;
        if (tier.equals("Elit")) userLevel = 3;

        if (userLevel < requiredLevel) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Bu şarkı için en az daha üst bir pakete sahip olmanız gerekiyor.");
            err.put("requiredTier", requiredLevel);
            return ResponseEntity.status(403).body(err);
        }

        String[] urls = {
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
        };
        int index = (int) (id % urls.length);
        
        Map<String, Object> response = new HashMap<>();
        response.put("audioUrl", urls[index]);
        response.put("title", "Song " + id);
        
        // ML dinleme geçmişi kaydı mock
        System.out.println("ML History Recorded for User: " + user + " Song: " + id);
        
        return ResponseEntity.ok(response);
    }
'''

import re
c = re.sub(r'@GetMapping.*?public Map<String, Object> playSong.*?return response;\n    }', replacement.strip(), c, flags=re.DOTALL)
c = c.replace('import org.springframework.web.bind.annotation.RestController;', 'import org.springframework.web.bind.annotation.RestController;\nimport org.springframework.http.ResponseEntity;')

with open(path, 'w') as f:
    f.write(c)
