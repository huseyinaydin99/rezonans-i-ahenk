with open('docker-compose.yml', 'r') as f:
    content = f.read()

content = content.replace('image: minio/minio:RELEASE.2023-05-18T00-05-36Z', 'image: minio/minio:latest')

with open('docker-compose.yml', 'w') as f:
    f.write(content)
