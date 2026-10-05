with open('docker-compose.yml', 'r') as f:
    content = f.read()

content = content.replace('image: minio/minio', 'image: quay.io/minio/minio')

with open('docker-compose.yml', 'w') as f:
    f.write(content)
