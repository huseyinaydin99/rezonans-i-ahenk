import os

# Fix infrastructure Cache config
path1 = 'music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/cache/HazelcastConfig.java'
with open(path1, 'r') as f:
    c1 = f.read()

if 'import com.hazelcast.config.JoinConfig;' not in c1:
    c1 = c1.replace('import com.hazelcast.config.MapConfig;', 'import com.hazelcast.config.JoinConfig;\nimport com.hazelcast.config.MapConfig;')
    
c1 = c1.replace('config.setClusterName("music-platform-cluster");', 'config.setClusterName("music-platform-cluster");\n        JoinConfig join = config.getNetworkConfig().getJoin();\n        join.getMulticastConfig().setEnabled(false);\n        join.getTcpIpConfig().setEnabled(true).addMember("127.0.0.1");')

with open(path1, 'w') as f:
    f.write(c1)

# Fix web RateLimit config
path2 = 'music-web/src/main/java/tr/com/huseyinaydin/web/config/RateLimitConfig.java'
with open(path2, 'r') as f:
    c2 = f.read()

if 'import com.hazelcast.config.JoinConfig;' not in c2:
    c2 = c2.replace('import com.hazelcast.config.Config;', 'import com.hazelcast.config.Config;\nimport com.hazelcast.config.JoinConfig;')

c2 = c2.replace('config.setInstanceName("music-rate-limit-instance");', 'config.setInstanceName("music-rate-limit-instance");\n        JoinConfig join = config.getNetworkConfig().getJoin();\n        join.getMulticastConfig().setEnabled(false);\n        join.getTcpIpConfig().setEnabled(true).addMember("127.0.0.1");')

with open(path2, 'w') as f:
    f.write(c2)
