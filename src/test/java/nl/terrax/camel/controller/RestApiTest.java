package nl.terrax.camel.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import io.micrometer.core.instrument.MeterRegistry;
import nl.terrax.camel.model.Beer;
import nl.terrax.camel.model.BeerType;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
//@SpringBootTest(properties = {"camel.springboot.java-routes-exclude-pattern=nl/terrax/camel/route/largefiles/*"})
@SpringBootTest(properties = {"camel.springboot.route-filter-include-pattern=beerservice*"})
@ActiveProfiles("test")
public class RestApiTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    @MockBean(name="hazelcastInstance")
    private HazelcastInstance hazelcastInstanceMock;

    @Mock
    private IMap<Object, Object> beerMapMock;

    @Value("${hazelcast.map.beer-cache}")
    private String beerMap;

    @SuppressWarnings("unused")
    @EndpointInject("mock:post-beer")
    private MockEndpoint mockPostBeer;

    private final Beer lager = new Beer("Bavaria", BeerType.LAGER);
    private final Beer ale = new Beer("Bavaria", BeerType.ALE);

    @BeforeEach
    void setUp() {
        when(hazelcastInstanceMock.getMap(eq(beerMap))).thenReturn(beerMapMock);
    }

    @Test
    @DirtiesContext
    void whenPostDifferentBeerTypesExpectCorrectMetrics() throws InterruptedException {
        // given
        mockPostBeer.expectedMessageCount(5);

        // when
        producerTemplate.sendBody("direct:post-beer", lager);
        producerTemplate.sendBody("direct:post-beer", lager);
        producerTemplate.sendBody("direct:post-beer", ale);
        producerTemplate.sendBody("direct:post-beer", lager);
        producerTemplate.sendBody("direct:post-beer", ale);

        // then
        mockPostBeer.assertIsSatisfied();
        assertEquals(3.0, meterRegistry.get("beer.orders").tag("type", "lager").counter().count());
        assertEquals(2.0, meterRegistry.get("beer.orders").tag("type", "ale").counter().count());
    }

}