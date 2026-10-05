with open('docker-compose.yml', 'r') as f:
    content = f.read()

import re
content = re.sub(
    r'minio-init:\n.*?networks:\n      - music-network',
    r'''minio-init:
    image: alpine:latest
    container_name: music_minio_init
    depends_on:
      - minio
    entrypoint: >
      /bin/sh -c "
      apk add --no-cache curl;
      curl -O https://dl.min.io/client/mc/release/linux-amd64/mc;
      chmod +x mc;
      ./mc alias set myminio http://minio:9000 minioadmin minioadmin;
      ./mc mb myminio/music-audio-bucket || true;
      ./mc mb myminio/music-image-bucket || true;
      ./mc anonymous set download myminio/music-audio-bucket;
      ./mc anonymous set download myminio/music-image-bucket;
      "
    networks:
      - music-network''',
    content, flags=re.DOTALL
)

with open('docker-compose.yml', 'w') as f:
    f.write(content)
