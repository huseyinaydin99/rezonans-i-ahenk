import os

eh_path = 'music-web/src/main/java/tr/com/huseyinaydin/web/config/jsf/GlobalExceptionHandler.java'
if os.path.exists(eh_path):
    with open(eh_path, 'r') as f:
        content = f.read()
    content = content.replace('private public Throwable getRootCause', 'public Throwable getRootCause')
    content = content.replace('protected public Throwable getRootCause', 'public Throwable getRootCause')
    with open(eh_path, 'w') as f:
        f.write(content)

