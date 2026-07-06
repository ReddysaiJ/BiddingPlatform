package com.example.auction.config;

import com.example.auction.ApplicationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String WINNER_QUEUE = "auction.service.winner.declared";
    public static final String WINNER_ROUTING_KEY = "auction.winner";

    private final ApplicationProperties applicationProperties;

    public RabbitMQConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    TopicExchange auctionExchange() {
        return new TopicExchange(applicationProperties.auctionExchange(), true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public Queue auctionWinnerQueue() {
        return QueueBuilder.durable(WINNER_QUEUE).build();
    }

    @Bean
    public Binding auctionWinnerBinding() {
        return BindingBuilder
                .bind(auctionWinnerQueue())
                .to(auctionExchange())
                .with(WINNER_ROUTING_KEY);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                               Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}
