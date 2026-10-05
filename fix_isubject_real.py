with open('music-application/src/main/java/tr/com/huseyinaydin/application/notifications/observer/ISubject.java', 'r') as f:
    content = f.read()

# Remove the incorrectly placed import
content = content.replace('import tr.com.huseyinaydin.domain.song.Song;\npackage', 'package')

# Add it after the package declaration
content = content.replace('package tr.com.huseyinaydin.application.notifications.observer;', 
                          'package tr.com.huseyinaydin.application.notifications.observer;\n\nimport tr.com.huseyinaydin.domain.song.Song;\n')

with open('music-application/src/main/java/tr/com/huseyinaydin/application/notifications/observer/ISubject.java', 'w') as f:
    f.write(content)
