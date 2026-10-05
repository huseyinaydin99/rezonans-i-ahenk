with open('docker-compose.yml', 'r') as f:
    content = f.read()

content = content.replace('image: minio/mc', 'image: minio/minio:RELEASE.2023-05-18T00-05-36Z')
content = content.replace('/usr/bin/mc', 'mc')
# Because the minio image includes the mc binary? 
# Actually minio/minio does NOT include mc out of the box usually.
