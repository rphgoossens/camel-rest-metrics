package nl.terrax.camel.processor;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.AbstractEntryProcessor;
import io.micrometer.core.instrument.MeterRegistry;
import nl.terrax.camel.model.Beer;
import nl.terrax.camel.model.BeerSummary;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BeerOrderService {

    private final MeterRegistry meterRegistry;
    private final HazelcastInstance hazelcastInstance;
    private final String beerMap;

    // TODO: remove dependency on hazelcast and possibly on the meterRegistry as well
    public BeerOrderService(MeterRegistry meterRegistry,
                            @Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance,
                            @Value("${hazelcast.map.beer-cache}") String beerMap
    ) {
        this.meterRegistry = meterRegistry;
        this.hazelcastInstance = hazelcastInstance;
        this.beerMap = beerMap;
    }

    @SuppressWarnings("unused")
    public void postBeer(Exchange exchange) {
        Beer beer = exchange.getIn().getBody(Beer.class);

        switch (beer.getType()) {
            case LAGER:
                meterRegistry.counter("beer.orders", "type", "lager").increment();
                break;
            case ALE:
                meterRegistry.counter("beer.orders", "type", "ale").increment();
                break;
            default:
        }

        increaseTotalInCache(beer);

    }

    @SuppressWarnings("unused")
    public BeerSummary getBeer(@Header("name") String name) {
        int totalBeers = (int) hazelcastInstance.getMap(beerMap).getOrDefault(name, 0);

        return new BeerSummary(name, totalBeers);
    }

    private void increaseTotalInCache(Beer beer) {
        final IMap<String, Integer> map = hazelcastInstance.getMap(beerMap);
        map.executeOnKey(beer.getName(), new ValueModifier<String>());
    }

    static class ValueModifier<K> extends AbstractEntryProcessor<K, Integer> {
        @Override
        public Object process(Map.Entry<K, Integer> entry) {
            Integer value = entry.getValue();
            if (value == null) {
                value = 0;
            }
            entry.setValue(++value);
            return value;
        }
    }

}
