package nl.terrax.camel.config;

import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;

@Configuration
public class JmsConfig {


    @Bean
    public ActiveMQJMSConnectionFactory artemisConnectionFactory() {
        ActiveMQJMSConnectionFactory activeMQConnectionFactory = new ActiveMQJMSConnectionFactory("tcp://localhost:61616");
        activeMQConnectionFactory.setUser("admin");
        activeMQConnectionFactory.setPassword("admin");
        //activeMQConnectionFactory.setMinLargeMessageSize(10);
        //activeMQConnectionFactory.setCompressLargeMessage(true);
        //activeMQConnectionFactory.setCacheLargeMessagesClient(true);

        return activeMQConnectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(artemisConnectionFactory());

        return cachingConnectionFactory;
    }

//    public JmsTransactionManager jmsTransactionManager() {
//        JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
//        jmsTransactionManager.setConnectionFactory(artemisConnectionFactory());
//
//        return jmsTransactionManager;
//    }

}
