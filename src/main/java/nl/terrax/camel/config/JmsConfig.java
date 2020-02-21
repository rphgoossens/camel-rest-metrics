package nl.terrax.camel.config;

import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;

@Configuration
@Profile("!test")
public class JmsConfig {

    @Value("${artemis.broker-url}")
    private String artemisBrokerUrl;

    @Value("${artemis.user}")
    private String artemisUser;

    @Value("${artemis.password}")
    private String artemisPassword;

    @Bean
    public ActiveMQJMSConnectionFactory artemisConnectionFactory() {
        ActiveMQJMSConnectionFactory activeMQConnectionFactory = new ActiveMQJMSConnectionFactory(artemisBrokerUrl);
        activeMQConnectionFactory.setUser(artemisUser);
        activeMQConnectionFactory.setPassword(artemisPassword);
        activeMQConnectionFactory.setCompressLargeMessage(true);

        return activeMQConnectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(artemisConnectionFactory());

        return cachingConnectionFactory;
    }

    @Bean
    public JmsTransactionManager jmsTransactionManager() {
        JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
        jmsTransactionManager.setConnectionFactory(artemisConnectionFactory());

        return jmsTransactionManager;
    }

}
