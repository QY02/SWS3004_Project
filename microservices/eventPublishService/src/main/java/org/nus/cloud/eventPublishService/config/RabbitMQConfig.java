package org.nus.cloud.eventPublishService.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public FanoutExchange fanout() {
        return new FanoutExchange("eventBriefDataFanout", true, false);
    }
}
