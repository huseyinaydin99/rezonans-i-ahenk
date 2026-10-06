path = 'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/SongController.java'
with open(path, 'r') as f:
    c = f.read()

import re

# Add autowiring for Kafka producer if not exists
if 'ListenEventProducer' not in c:
    c = c.replace('public class SongController {', 'public class SongController {\n\n    @org.springframework.beans.factory.annotation.Autowired\n    private tr.com.huseyinaydin.infrastructure.kafka.ListenEventProducer listenEventProducer;\n')

c = c.replace('System.out.println("ML History Recorded for User: " + user + " Song: " + id);', 'listenEventProducer.sendListenEvent(user.toString(), id);')

with open(path, 'w') as f:
    f.write(c)
