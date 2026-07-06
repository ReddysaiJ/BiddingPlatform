package com.example.auction.jobs;

import com.example.auction.domain.AuctionService;
import com.example.auction.domain.models.AuctionEventDTO;
import com.example.auction.domain.models.AuctionWinnerDeclaredEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WinnerDeclaredEventListener {
    private static final Logger log = LoggerFactory.getLogger(WinnerDeclaredEventListener.class);

    private final AuctionService auctionService;
    private final ObjectMapper objectMapper;

    public WinnerDeclaredEventListener(AuctionService auctionService, ObjectMapper objectMapper) {
        this.auctionService = auctionService;
        this.objectMapper   = objectMapper;
    }

    @RabbitListener(queues = "#{@auctionWinnerQueue.name}")
    public void onWinnerDeclared(AuctionEventDTO event) {
        AuctionWinnerDeclaredEvent winner = objectMapper.convertValue(event.data(), AuctionWinnerDeclaredEvent.class);

        log.info("AUCTION_WINNER_DECLARED received from bid-service | auctionId={} | winner={} | price={}",
                winner.auctionId(), winner.winnerUserId(), winner.finalPrice());

        auctionService.declareWinner(winner.auctionId(), winner.winnerUserId(), winner.finalPrice());
    }
}
