package nl.terrax.camel.processor;

import io.micrometer.core.instrument.MeterRegistry;
import nl.terrax.camel.model.Beer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class BeerOrderProcessor implements Processor {

    private final MeterRegistry meterRegistry;

    public BeerOrderProcessor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void process(Exchange exchange) {
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

    }
}
