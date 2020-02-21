package nl.terrax.camel.route;

import org.apache.camel.builder.RouteBuilder;

import static org.apache.camel.LoggingLevel.INFO;

public class FileThroughArtemisMQRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:/home/rphgoossens/inbox")
                .log(INFO, "file opgepikt")
                .inOnly("jms:queue:file-queue");

        from("jms:queue:file-queue")
                .streamCaching()
                .log(INFO, "amq message opgepikt")
                .to("file:/home/rphgoossens/outbox");

    }

}
