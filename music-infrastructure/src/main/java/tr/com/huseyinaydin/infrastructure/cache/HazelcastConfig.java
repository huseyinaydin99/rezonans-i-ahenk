package tr.com.huseyinaydin.infrastructure.cache;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setClusterName("music-platform-cluster");

        // "cqrs-cache" isimli map (önbellek) için yapılandırma
        MapConfig mapConfig = new MapConfig("cqrs-cache");
        mapConfig.setTimeToLiveSeconds(3600); // 1 saat TTL
        
        EvictionConfig evictionConfig = new EvictionConfig()
                .setEvictionPolicy(EvictionPolicy.LRU) // En az kullanılanları temizle
                .setMaxSizePolicy(MaxSizePolicy.PER_NODE)
                .setSize(1000); // Node başına max 1000 kayıt
                
        mapConfig.setEvictionConfig(evictionConfig);
        config.addMapConfig(mapConfig);

        System.out.println("Hazelcast dağıtık önbellek (Distributed Cache) kümesi başlatılıyor...");
        return Hazelcast.newHazelcastInstance(config);
    }
}
