package com.example.chat.config;

import com.example.chat.ApplicationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String WINNER_ROUTING_KEY = "auction.winner.declared";
    public static final String CHAT_WINNER_QUEUE  = "chat.winner.declared";

    private final ApplicationProperties props;

    public RabbitMQConfig(ApplicationProperties props) {
        this.props = props;
    }

    @Bean
    public TopicExchange auctionExchange() {
        return new TopicExchange(props.auctionExchange(), true, false);
    }

    @Bean
    public Queue chatWinnerQueue() {
        return QueueBuilder.durable(CHAT_WINNER_QUEUE).build();
    }

    @Bean
    public Binding chatWinnerBinding() {
        return BindingBuilder
                .bind(chatWinnerQueue())
                .to(auctionExchange())
                .with(WINNER_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);
        return factory;
    }
}
