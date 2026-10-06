path = 'music-application/src/main/java/tr/com/huseyinaydin/application/home/services/HomeServiceImpl.java'
with open(path, 'r') as f:
    s = f.read()

s = s.replace('"https://minio/blinding.jpg"', '"https://images.unsplash.com/photo-1493225457124-a1a2a53b51ee?auto=format&fit=crop&w=400&q=80"')
s = s.replace('"https://minio/shape.jpg"', '"https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=400&q=80"')
s = s.replace('"https://minio/queen.jpg"', '"https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80"')
s = s.replace('"https://minio/vampire.jpg"', '"https://images.unsplash.com/photo-1493225457124-a1a2a53b51ee?auto=format&fit=crop&w=400&q=80"')
s = s.replace('"https://minio/doja.jpg"', '"https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80"')
s = s.replace('"https://minio/starboy.jpg"', '"https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=400&q=80"')
s = s.replace('"https://minio/led.jpg"', '"https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80"')

# Give distinct IDs
s = s.replace('new SongSnippet(1L, "Shape of You"', 'new SongSnippet(2L, "Shape of You"')
s = s.replace('new SongSnippet(1L, "Bohemian Rhapsody"', 'new SongSnippet(3L, "Bohemian Rhapsody"')
s = s.replace('new SongSnippet(1L, "Vampire"', 'new SongSnippet(4L, "Vampire"')
s = s.replace('new SongSnippet(1L, "Paint The Town Red"', 'new SongSnippet(5L, "Paint The Town Red"')
s = s.replace('new SongSnippet(1L, "Starboy"', 'new SongSnippet(6L, "Starboy"')
s = s.replace('new SongSnippet(1L, "Stairway To Heaven"', 'new SongSnippet(7L, "Stairway To Heaven"')

with open(path, 'w') as f:
    f.write(s)
