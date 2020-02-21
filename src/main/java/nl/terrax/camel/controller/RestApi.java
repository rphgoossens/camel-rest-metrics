package nl.terrax.camel.controller;

import nl.terrax.camel.model.Beer;
import nl.terrax.camel.processor.BeerOrderProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

import static org.apache.camel.LoggingLevel.INFO;

@Component
public class RestApi extends RouteBuilder {

    private final BeerOrderProcessor beerOrderProcessor;

    RestApi(BeerOrderProcessor beerOrderProcessor) {
        this.beerOrderProcessor = beerOrderProcessor;
    }

    @Override
    public void configure() {

        rest("/api/").description("Beer Service")
                .id("api-route")
                .post("/beer")
                .produces(MediaType.APPLICATION_JSON)
                .consumes(MediaType.APPLICATION_JSON)
                .bindingMode(RestBindingMode.auto)
                .type(Beer.class)
                .enableCORS(true)
                .to("direct:remote-service");

        from("direct:remote-service")
                .routeId("direct-route")
                .process(beerOrderProcessor)
                .log(INFO, "Beer ${body.name} of type ${body.type} posted")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .to("mock:post-beer");
    }

}