with open('docker-compose.yml', 'r') as f:
    content = f.read()

content = content.replace('bitnami/spark:latest', 'bde2020/spark-master:3.3.0-hadoop3.3')
content = content.replace('bitnami/spark:3.4.1', 'bde2020/spark-master:3.3.0-hadoop3.3')
content = content.replace('SPARK_MODE=master', 'INIT_DAEMON_STEP=setup_spark')
content = content.replace('SPARK_MODE=worker', 'INIT_DAEMON_STEP=setup_spark')
content = content.replace('SPARK_MASTER_URL=spark://spark-master:7077', 'SPARK_MASTER=spark://spark-master:7077')

with open('docker-compose.yml', 'w') as f:
    f.write(content)
