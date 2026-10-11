package tr.com.huseyinaydin.infrastructure.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
public class HazelcastConfig {
    private static final Logger log = LoggerFactory.getLogger(HazelcastConfig.class);

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setClusterName("music-platform-cluster");
        config.setInstanceName("music-platform-hazelcast-instance");
        
        JoinConfig join = config.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true).addMember("127.0.0.1");

        // Dağıtık önbellek haritaları (Distributed Cache Maps)
        config.addMapConfig(createMapConfig("songsCache", 3600, 5000));
        config.addMapConfig(createMapConfig("homeDashboardCache", 900, 1000));
        config.addMapConfig(createMapConfig("genresCache", 7200, 1000));
        config.addMapConfig(createMapConfig("recommendationsCache", 1800, 2000));
        config.addMapConfig(createMapConfig("queryCache", 3600, 5000));
        config.addMapConfig(createMapConfig("referenceDataCache", 7200, 1000));
        config.addMapConfig(createMapConfig("cqrs-cache", 3600, 2000));
        config.addMapConfig(createMapConfig("defaultCache", 1800, 5000));

        log.info("Hazelcast dağıtık önbellek (Distributed Cache) kümesi yapılandırıldı ve başlatılıyor...");
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    @Primary
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        log.info("HazelcastCacheManager Spring Caching altyapısına birincil (Primary) CacheManager olarak kaydedildi.");
        return new HazelcastCacheManager(hazelcastInstance);
    }

    private MapConfig createMapConfig(String name, int ttlSeconds, int maxSize) {
        MapConfig mapConfig = new MapConfig(name);
        mapConfig.setTimeToLiveSeconds(ttlSeconds);
        EvictionConfig evictionConfig = new EvictionConfig()
                .setEvictionPolicy(EvictionPolicy.LRU)
                .setMaxSizePolicy(MaxSizePolicy.PER_NODE)
                .setSize(maxSize);
        mapConfig.setEvictionConfig(evictionConfig);
        return mapConfig;
    }
}
