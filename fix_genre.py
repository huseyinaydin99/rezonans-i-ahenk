with open('music-domain/src/main/java/tr/com/huseyinaydin/domain/genre/Genre.java', 'r') as f:
    content = f.read()

content = content.replace('this.description = description;', 'this.imageUrl = description;')
content = content.replace('public Genre(String name, String description)', 'public Genre(String name, String description)')

with open('music-domain/src/main/java/tr/com/huseyinaydin/domain/genre/Genre.java', 'w') as f:
    f.write(content)
