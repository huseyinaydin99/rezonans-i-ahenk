package tr.com.huseyinaydin.web.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.github.bucket4j.grid.hazelcast.HazelcastProxyManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setClusterName("music-rate-limit-cluster");
        config.setInstanceName("music-rate-limit-instance");
        JoinConfig join = config.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true).addMember("127.0.0.1");
        return Hazelcast.getOrCreateHazelcastInstance(config);
    }

    @Bean
    public HazelcastProxyManager<String> hazelcastProxyManager(HazelcastInstance hazelcastInstance) {
        IMap<String, byte[]> map = hazelcastInstance.getMap("rate-limits");
        return new HazelcastProxyManager<>(map);
    }
}
