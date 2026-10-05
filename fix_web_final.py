import os
import re

# 1. Fix GlobalExceptionHandler
eh_path = 'music-web/src/main/java/tr/com/huseyinaydin/web/config/jsf/GlobalExceptionHandler.java'
if os.path.exists(eh_path):
    with open(eh_path, 'r') as f:
        content = f.read()
    content = content.replace('Throwable getRootCause(Throwable t)', 'public Throwable getRootCause(Throwable t)')
    with open(eh_path, 'w') as f:
        f.write(content)

# 2. Fix Mediator queries
files_to_fix = [
    'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/GenresController.java',
    'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/ItunesController.java',
    'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/RecommendationsController.java',
    'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/SearchController.java',
    'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/SongsController.java',
    'music-web/src/main/java/tr/com/huseyinaydin/web/jsf/GenreBean.java',
    'music-web/src/main/java/tr/com/huseyinaydin/web/jsf/ItunesBean.java',
    'music-web/src/main/java/tr/com/huseyinaydin/web/jsf/SearchBean.java',
    'music-web/src/main/java/tr/com/huseyinaydin/web/jsf/SongBean.java',
    'music-web/src/main/java/tr/com/huseyinaydin/web/jsf/UserDashboardBean.java'
]

for fp in files_to_fix:
    if os.path.exists(fp):
        with open(fp, 'r') as f:
            content = f.read()
        
        # Replace mediator.send(new ...Query with mediator.query(new ...Query
        content = re.sub(r'mediator\.send\((new \w+Query)', r'mediator.query(\1', content)
        with open(fp, 'w') as f:
            f.write(content)

