package nl.terrax.camel;

import io.hawt.config.ConfigFacade;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.URL;

@SpringBootApplication
@EnableSwagger2
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static final String JAVA_SECURITY_AUTH_LOGIN_CONFIG = "java.security.auth.login.config";

    private final MeterRegistry meterRegistry;

    public Application(MeterRegistry meterRegistry){
        this.meterRegistry = meterRegistry;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        Counter.builder("beer.orders")    // 2- create a counter using the fluent API
                .tag("type", "ale")
                .description("The number of orders ever placed for Ale beers")
                .register(meterRegistry);
        Counter.builder("beer.orders")    // 2- create a counter using the fluent API
                .tag("type", "lager")
                .description("The number of orders ever placed for Lager beers")
                .register(meterRegistry);
    }

    /**
     * Configure facade to use authentication.
     *
     * @return config
     */
    @Bean(initMethod = "init")
    public ConfigFacade configFacade() {

        final URL loginResource = this.getClass().getClassLoader().getResource("login.conf");
        if (loginResource != null) {
            setSystemPropertyIfNotSet(JAVA_SECURITY_AUTH_LOGIN_CONFIG, loginResource.toExternalForm());
        }
        LOG.info("Using loginResource {} : {}", JAVA_SECURITY_AUTH_LOGIN_CONFIG, System
                .getProperty(JAVA_SECURITY_AUTH_LOGIN_CONFIG));

        final URL loginFile = this.getClass().getClassLoader().getResource("realm.properties");
        if (loginFile != null) {
            setSystemPropertyIfNotSet("login.file", loginFile.toExternalForm());
        }
        LOG.info("Using login.file : {}", System.getProperty("login.file"));

        return new ConfigFacade();
    }


    private void setSystemPropertyIfNotSet(final String key, final String value) {
        if (System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }
}