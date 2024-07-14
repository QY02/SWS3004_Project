package org.nus.cloud.eventGlobalDataService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${pod-index}")
    private String podIndex;

    @Bean
    public FanoutExchange fanout() {
        return new FanoutExchange("eventBriefDataFanout", true, false);
    }

    @Bean
    public Queue queue() {
        return new Queue("eventBriefDataQueue-" + podIndex, true);
    }

    @Bean
    public Binding binding(FanoutExchange fanoutExchange, Queue queue) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }
}
