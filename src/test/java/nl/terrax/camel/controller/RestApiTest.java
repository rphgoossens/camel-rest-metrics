package nl.terrax.camel.controller;

import io.micrometer.core.instrument.MeterRegistry;
import nl.terrax.camel.model.Beer;
import nl.terrax.camel.model.BeerType;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RestApiTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    @EndpointInject(uri = "mock:post-beer")
    private MockEndpoint mockPostBeer;

    private final Beer lager = new Beer("Bavaria", BeerType.LAGER);
    private final Beer ale = new Beer("Bavaria", BeerType.ALE);


    @Test
    @DirtiesContext
    void whenPostDifferentBeerTypesExpectCorrectMetrics() throws InterruptedException {
        // given
        mockPostBeer.expectedMessageCount(5);

        // when
        producerTemplate.sendBody("direct:remote-service", lager);
        producerTemplate.sendBody("direct:remote-service", lager);
        producerTemplate.sendBody("direct:remote-service", ale);
        producerTemplate.sendBody("direct:remote-service", lager);
        producerTemplate.sendBody("direct:remote-service", ale);

        // then
        mockPostBeer.assertIsSatisfied();
        assertEquals(3.0, meterRegistry.get("beer.orders").tag("type", "lager").counter().count());
        assertEquals(2.0, meterRegistry.get("beer.orders").tag("type", "ale").counter().count());
    }

}