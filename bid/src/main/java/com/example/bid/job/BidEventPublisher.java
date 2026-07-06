package com.example.bid.job;

import com.example.bid.domain.BidOutboxEventEntity;
import com.example.bid.domain.models.AuctionEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BidEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE = "auction.exchange";

    public BidEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(BidOutboxEventEntity event) {
        AuctionEventDTO envelope = new AuctionEventDTO(
                event.getEventId(),
                event.getEventType(),
                event.getAuctionId(),
                event.getCreatedAt(),
                event.getPayload()
        );

        String routingKey = switch (event.getEventType()) {
            case HIGHEST_BID_UPDATED     -> "auction.highest.bid";
            case AUCTION_WINNER_DECLARED -> "auction.winner";
            default -> throw new IllegalArgumentException("Unhandled event type: " + event.getEventType());
        };

        rabbitTemplate.convertAndSend(EXCHANGE, routingKey, envelope);
    }
}