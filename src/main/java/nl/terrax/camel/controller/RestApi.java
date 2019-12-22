package nl.terrax.camel.controller;

import nl.terrax.camel.model.Beer;
import nl.terrax.camel.processor.BeerOrderProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

import static org.apache.camel.LoggingLevel.INFO;

@Component
class RestApi extends RouteBuilder {

    private final BeerOrderProcessor beerOrderProcessor;

    RestApi(BeerOrderProcessor beerOrderProcessor) {
        this.beerOrderProcessor = beerOrderProcessor;
    }

    @Value("${server.port}")
    String serverPort;

    @Value("${api.path}")
    String contextPath;

    @Override
    public void configure() {

        // http://localhost:8080/camel/v2/api-docs
        restConfiguration().contextPath(contextPath) //
                .port(serverPort)
                .enableCORS(true)
                .apiContextPath("/v2/api-docs")
                .apiProperty("api.title", "Beer API with metrics")
                .apiProperty("api.version", "v1")
                .apiProperty("cors", "true") // cross-site
                .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true");

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