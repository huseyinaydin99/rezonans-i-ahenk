import os

p1 = 'music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/ElasticSong.java'
with open(p1, 'r') as f:
    c1 = f.read()
c1 = c1.replace('package tr.com.huseyinaydin.domain.song;', 'package tr.com.huseyinaydin.infrastructure;')
with open(p1, 'w') as f:
    f.write(c1)

p2 = 'music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/IElasticSongRepository.java'
with open(p2, 'r') as f:
    c2 = f.read()
c2 = c2.replace('package tr.com.huseyinaydin.domain.repositories;', 'package tr.com.huseyinaydin.infrastructure;')
c2 = c2.replace('import tr.com.huseyinaydin.domain.song.ElasticSong;', '')
with open(p2, 'w') as f:
    f.write(c2)
