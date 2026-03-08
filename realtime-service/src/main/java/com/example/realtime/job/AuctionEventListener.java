package com.example.realtime.job;

import com.example.realtime.domain.models.AuctionEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuctionEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    public AuctionEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "realtime.auction.events")
    public void handle(AuctionEventDTO event) {
        UUID auctionId = event.auctionId();

        String destination = switch(event.eventType()) {
            case AUCTION_WINNER_DECLARED -> "/topic/auction/" + auctionId + "/status";
            case AUCTION_UPDATED -> "/topic/auction/" + auctionId + "/highest-bid";
            default -> "/topic/auction/" + auctionId + "/events";
        };

        messagingTemplate.convertAndSend(destination, event.data());
    }
}