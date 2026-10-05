with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/MusicApplicationConfig.java', 'r') as f:
    content = f.read()

import re
content = re.sub(r'@Bean\s*public CommandBus commandBus\(Mediator mediator\) \{\s*return mediator;\s*\}', '', content)
content = re.sub(r'@Bean\s*public QueryBus queryBus\(Mediator mediator\) \{\s*return mediator;\s*\}', '', content)

with open('music-application/src/main/java/tr/com/huseyinaydin/application/config/MusicApplicationConfig.java', 'w') as f:
    f.write(content)
