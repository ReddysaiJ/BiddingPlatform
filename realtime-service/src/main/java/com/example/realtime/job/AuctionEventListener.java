package com.example.realtime.job;

import com.example.realtime.domain.AuctionRealtimeCache;
import com.example.realtime.domain.models.AuctionEventDTO;
import com.example.realtime.domain.models.HighestBidDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuctionEventListener {
    private static final Logger log = LoggerFactory.getLogger(AuctionEventListener.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final AuctionRealtimeCache cache;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuctionEventListener(SimpMessagingTemplate messagingTemplate, AuctionRealtimeCache cache) {
        this.messagingTemplate = messagingTemplate;
        this.cache = cache;
    }

    @RabbitListener(queues = "realtime.auction.events")
    public void handle(AuctionEventDTO event) {

        UUID auctionId = event.auctionId();
        String topic = "/topic/auction/" + auctionId;

        switch (event.eventType()) {

            case AUCTION_CREATED -> {
                cache.createAuction(auctionId, event.data());
                messagingTemplate.convertAndSend("/topic/auctions", event.data());
            }

            case AUCTION_UPDATED -> {
                cache.updateAuctionDetails(auctionId, event.data());
                messagingTemplate.convertAndSend(topic + "/events", event.data());
            }

            case AUCTION_DELETED -> {
                cache.deleteAuction(auctionId);
                messagingTemplate.convertAndSend(topic + "/events", "DELETED");
            }

            case AUCTION_OPEN -> {
                cache.updateStatus(auctionId, "OPEN");
                messagingTemplate.convertAndSend(topic + "/status", "OPEN");
            }

            case AUCTION_CLOSED -> {
                cache.updateStatus(auctionId, "CLOSED");
                messagingTemplate.convertAndSend(topic + "/status", "CLOSED");
            }

            case HIGHEST_BID_UPDATED -> {
                try {
                    HighestBidDTO bid = objectMapper.convertValue(event.data(), HighestBidDTO.class);
                    cache.updateHighestBid(auctionId, bid);
                    messagingTemplate.convertAndSend(topic + "/highestBid", bid);
                } catch (Exception e) {
                    throw new ClassCastException("Conversion failed: " + e.getMessage());
                }
            }

            case AUCTION_WINNER_DECLARED -> {
                cache.updateWinner(auctionId, event.data());
                messagingTemplate.convertAndSend(topic + "/winner", event.data());
            }
        }
    }
}