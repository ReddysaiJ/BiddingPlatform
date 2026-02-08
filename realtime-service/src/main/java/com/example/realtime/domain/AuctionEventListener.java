package com.example.realtime.domain;

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

        switch(event.eventType()) {

            case BID_PLACED ->
                    messagingTemplate.convertAndSend(
                            "/topic/auction/" + auctionId + "/bids",
                            event.payload()
                    );

            case AUCTION_WINNER_DECLARED ->
                    messagingTemplate.convertAndSend(
                            "/topic/auction/" + auctionId + "/status",
                            event.payload()
                    );
        }
    }
}
