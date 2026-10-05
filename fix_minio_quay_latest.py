with open('docker-compose.yml', 'r') as f:
    content = f.read()

content = content.replace('image: minio/minio:latest', 'image: quay.io/minio/minio:latest')
content = content.replace('image: minio/minio', 'image: quay.io/minio/minio:latest')

with open('docker-compose.yml', 'w') as f:
    f.write(content)
