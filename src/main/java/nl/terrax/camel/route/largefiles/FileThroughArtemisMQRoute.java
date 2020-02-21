package nl.terrax.camel.route.largefiles;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static org.apache.camel.LoggingLevel.INFO;

@Component
public class FileThroughArtemisMQRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("file:/home/rphgoossens/inbox")
                .log(INFO, "Picked up file")
                .inOnly("jms:queue:file-queue");

        from("jms:queue:file-queue")
                .streamCaching()
                .log(INFO, "Dropped off file")
                .to("file:/home/rphgoossens/outbox");

    }

}
