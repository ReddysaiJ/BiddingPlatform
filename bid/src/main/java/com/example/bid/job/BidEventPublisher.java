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
        AuctionEventDTO auctionEvent = new AuctionEventDTO(
                event.getEventId(),
                event.getEventType(),
                event.getAuctionId(),
                event.getCreatedAt(),
                event.getPayload()
        );

        String routingKey = "auction.highest.bid";
        rabbitTemplate.convertAndSend(EXCHANGE, routingKey, auctionEvent);
    }
}