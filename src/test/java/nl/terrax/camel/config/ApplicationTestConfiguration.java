package nl.terrax.camel.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class ApplicationTestConfiguration {
}
