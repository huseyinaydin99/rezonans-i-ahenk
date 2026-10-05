with open('docker-compose.yml', 'r') as f:
    content = f.read()

content = content.replace('APP_PASSWORD=music_app_pwd', 'APP_USER_PASSWORD=music_app_pwd')

with open('docker-compose.yml', 'w') as f:
    f.write(content)
