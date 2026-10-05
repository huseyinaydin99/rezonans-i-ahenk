with open('music-application/src/main/java/tr/com/huseyinaydin/application/notifications/observer/ISubject.java', 'r') as f:
    content = f.read()

content = "import tr.com.huseyinaydin.domain.song.Song;\n" + content

with open('music-application/src/main/java/tr/com/huseyinaydin/application/notifications/observer/ISubject.java', 'w') as f:
    f.write(content)
