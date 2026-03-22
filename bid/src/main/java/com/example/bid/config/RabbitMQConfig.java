package com.example.bid.config;

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

    public static final String AUCTION_EXCHANGE = "auction.exchange";

    public static final String AUCTION_QUEUE = "bid.auction.events";
    public static final String AUCTION_RETRY_QUEUE = "bid.auction.events.retry";
    public static final String AUCTION_DLQ = "bid.auction.events.dlq";

    public static final String RETRY_ROUTING_KEY = "auction.retry";

    @Bean
    public TopicExchange auctionExchange() {
        return new TopicExchange(AUCTION_EXCHANGE);
    }

    @Bean
    public Queue auctionQueue() {
        return QueueBuilder.durable(AUCTION_QUEUE)
                .withArgument("x-dead-letter-exchange", AUCTION_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RETRY_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue auctionRetryQueue() {
        return QueueBuilder.durable(AUCTION_RETRY_QUEUE)
                .withArgument("x-message-ttl", 5000)
                .withArgument("x-dead-letter-exchange", AUCTION_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "auction.created")
                .build();
    }

    @Bean
    public Queue auctionDLQ() {
        return QueueBuilder.durable(AUCTION_DLQ).build();
    }

    @Bean
    public Binding createdBinding() {
        return BindingBuilder
                .bind(auctionQueue())
                .to(auctionExchange())
                .with("auction.created");
    }

    @Bean
    public Binding updatedBinding() {
        return BindingBuilder
                .bind(auctionQueue())
                .to(auctionExchange())
                .with("auction.updated");
    }

    @Bean
    public Binding openBinding() {
        return BindingBuilder
                .bind(auctionQueue())
                .to(auctionExchange())
                .with("auction.open");
    }

    @Bean
    public Binding closedBinding() {
        return BindingBuilder
                .bind(auctionQueue())
                .to(auctionExchange())
                .with("auction.closed");
    }

    @Bean
    public Binding winnerBinding() {
        return BindingBuilder
                .bind(auctionQueue())
                .to(auctionExchange())
                .with("auction.winner");
    }

    @Bean
    public Binding retryBinding() {
        return BindingBuilder
                .bind(auctionRetryQueue())
                .to(auctionExchange())
                .with(RETRY_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}