package com.example.auction.domain;

import com.example.auction.ApplicationProperties;
import com.example.auction.domain.exception.InvalidEventTypeException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuctionEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ApplicationProperties properties;

    public AuctionEventPublisher(RabbitTemplate rabbitTemplate, ApplicationProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    public void publish(AuctionOutboxEventEntity event) {
        String rk = routingKey(event.getEventType().name());
        rabbitTemplate.convertAndSend(properties.auctionExchange(), rk, event.getPayload());
    }

    private String routingKey(String eventType) {
        return switch (eventType) {
            case "AUCTION_CREATED" -> "auction.created";
            case "AUCTION_UPDATED" -> "auction.updated";
            case "AUCTION_DELETED" -> "auction.deleted";
            case "AUCTION_OPEN" -> "auction.open";
            case "AUCTION_CLOSED"  -> "auction.closed";
            case "AUCTION_WINNER_DECLARED" -> "auction.winner";
            default -> throw new InvalidEventTypeException(eventType);
        };
    }
}

