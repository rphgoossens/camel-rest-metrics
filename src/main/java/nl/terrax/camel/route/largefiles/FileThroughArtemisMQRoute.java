package nl.terrax.camel.route.largefiles;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static org.apache.camel.LoggingLevel.INFO;

@Component
public class FileThroughArtemisMQRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("file:/home/rphgoossens/inbox").routeId("artemis.1")
                .log(INFO, "Picked up file")
                .inOnly("jms:queue:file-queue");

        from("jms:queue:file-queue").routeId("artemis.2")
                .streamCaching()
                .log(INFO, "Dropped off file")
                .to("file:/home/rphgoossens/outbox");

    }

}
