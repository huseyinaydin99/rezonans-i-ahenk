path = 'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/SongController.java'
with open(path, 'r') as f:
    c = f.read()

replacement = '''
    @GetMapping(value = {"/Song/Play/{id}", "/api/Song/Play/{id}"})
    public Map<String, Object> playSong(@PathVariable("id") Long id) {
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
        return response;
    }
'''

import re
c = re.sub(r'@GetMapping.*?public Map<String, Object> playSong.*?return response;\n    }', replacement.strip(), c, flags=re.DOTALL)

with open(path, 'w') as f:
    f.write(c)
