package nl.terrax.camel.controller;

import nl.terrax.camel.model.Beer;
import nl.terrax.camel.model.BeerSummary;
import nl.terrax.camel.processor.BeerOrderService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

import static org.apache.camel.LoggingLevel.INFO;

@Component
public class RestApi extends RouteBuilder {

    public static final String BEERSERVICE_GET_ROUTE = "beerservice.get-route";
    public static final String BEERSERVICE_POST_ROUTE = "beerservice.post-route";
    private final BeerOrderService beerOrderService;

    RestApi(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @Override
    public void configure() {

        rest("/api/").description("Beer Service")
                .id("api-route")
                .get("beer/{name}")
                .produces(MediaType.APPLICATION_JSON)
                .bindingMode(RestBindingMode.auto)
                .outType(BeerSummary.class)
                .to("direct:get-beer")
                .post("/beer")
                .consumes(MediaType.APPLICATION_JSON)
                .produces(MediaType.APPLICATION_JSON)
                .bindingMode(RestBindingMode.auto)
                .type(Beer.class)
                .to("direct:post-beer");

        from("direct:get-beer")
                .routeId(BEERSERVICE_GET_ROUTE)
                .log(INFO, "Beer ${header.name} requested")
                .bean(beerOrderService, "getBeer")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .to("mock:get-beer");


        from("direct:post-beer")
                .routeId(BEERSERVICE_POST_ROUTE)
                .bean(beerOrderService, "postBeer")
                .log(INFO, "Beer ${body.name} of type ${body.type} posted")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .to("mock:post-beer");
    }

}