package nl.terrax.camel.config.cache;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class HazelcastConfiguration {

    @Bean
    public Config hazelCastConfig(@Qualifier("beerMapDBStore") MapStore<String, Integer> beerMapStore,
                                  BeerMapListener beerMapListener,
                                  @Value("${hazelcast.map.beer-cache}") String beerMap) {
        return new Config()
                .setInstanceName("hazelcast-instance")
                .addMapConfig(new MapConfig()
                        .addEntryListenerConfig(new EntryListenerConfig(beerMapListener, false, false))
                        .setName(beerMap)
                        .setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                        .setEvictionPolicy(EvictionPolicy.LRU)
                        .setMapStoreConfig(new MapStoreConfig()
                                .setEnabled(true)
                                .setImplementation(beerMapStore)
                                .setWriteDelaySeconds(10))
                        .setTimeToLiveSeconds(60));
    }

    @Bean
    HazelcastInstance hazelcastInstance(Config hazelCastConfig) {
        return Hazelcast.newHazelcastInstance(hazelCastConfig);
    }

}