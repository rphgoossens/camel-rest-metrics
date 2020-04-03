package nl.terrax.camel.config.cache;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.map.listener.EntryEvictedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class BeerMapListener implements HazelcastInstanceAware, EntryEvictedListener<String, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeerMapListener.class);
    private final String beerMap;
    private HazelcastInstance hazelcastInstance;

    public BeerMapListener(@Value("${hazelcast.map.beer-cache}") String beerMap) {
        this.beerMap = beerMap;
    }

    @Override
    public void entryEvicted(EntryEvent<String, Integer> entryEvent) {
        LOGGER.info("Evicting {} from cache", entryEvent.getKey());
        hazelcastInstance.getMap(this.beerMap).remove(entryEvent.getKey());
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

}