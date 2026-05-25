package com.example.bid.job;

import com.example.bid.domain.*;
import com.example.bid.domain.models.AuctionEventDTO;
import com.example.bid.domain.models.AuctionOpenEvent;
import com.example.bid.domain.models.AuctionWinnerDeclaredEvent;
import com.example.bid.domain.models.OutboxStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class BidAuctionEventHandlerService {

    private static final Logger log = LoggerFactory.getLogger(BidAuctionEventHandlerService.class);

    private final OpenAuctionsRepository openAuctionsRepository;
    private final BidRepository bidRepository;
    private final ObjectMapper objectMapper;
    private final ProcessedEventRepository processedEventRepository;

    public BidAuctionEventHandlerService(
            OpenAuctionsRepository openAuctionsRepository,
            BidRepository bidRepository,
            ObjectMapper objectMapper,
            ProcessedEventRepository processedEventRepository) {
        this.openAuctionsRepository = openAuctionsRepository;
        this.bidRepository = bidRepository;
        this.objectMapper = objectMapper;
        this.processedEventRepository = processedEventRepository;
    }

    public void handle(AuctionEventDTO event) {
        if (processedEventRepository.existsByEventId(event.eventId()))
            return;
        processedEventRepository.save(new ProcessedEventEntity(event.eventId(), OutboxStatus.SENT));

        switch (event.eventType()) {
            case AUCTION_CREATED -> handleAuctionCreated(event);
            case AUCTION_UPDATED -> handleAuctionUpdated(event);
            case AUCTION_DELETED -> handleAuctionDeleted(event);
            case AUCTION_OPEN -> handleAuctionOpen(event);
            case AUCTION_CLOSED -> handleAuctionClosedAndFinalizeAuction(event);
            case AUCTION_WINNER_DECLARED -> handleAuctionWinnerDeclared(event);
            default -> throw new IllegalArgumentException("Unknown event type");
        }
    }

    private void handleAuctionOpen(AuctionEventDTO event) {
        AuctionOpenEvent openEvent = objectMapper.convertValue(event.data(), AuctionOpenEvent.class);
        OpenAuctionsEntity open = new OpenAuctionsEntity(openEvent.auctionUid(), openEvent.userId());
        openAuctionsRepository.save(open);
        log.info("AUCTION_OPENED | auctionId={} | eventId={}", event.auctionId(), event.eventId());
    }

    private void handleAuctionCreated(AuctionEventDTO event) {
        log.info("AUCTION_CREATED | auctionId={} | eventId={}", event.auctionId(), event.eventId());
    }

    private void handleAuctionUpdated(AuctionEventDTO event) {
        log.info("AUCTION_UPDATED | auctionId={} | eventId={}", event.auctionId(), event.eventId());
    }

    private void handleAuctionDeleted(AuctionEventDTO event) {
        openAuctionsRepository.deleteById(event.auctionId());
        log.info("AUCTION_DELETED | auctionId={} | eventId={}", event.auctionId(), event.eventId());
    }

    private void handleAuctionClosedAndFinalizeAuction(AuctionEventDTO event) {
        openAuctionsRepository.deleteById(event.auctionId());
        bidRepository.findHighestBid(event.auctionId()).ifPresent(bid -> {
            bidRepository.markWinner(bid.getId());
//            try {
//                auctionGateway.declareWinner(event.auctionId(), bid.getUserId(), bid.getAmount());
//            } catch (Exception ex) {
//                log.error("Failed to notify Auction service about winner", ex);
//            }
        });

        log.info("AUCTION_CLOSED | auctionId={} | eventId={}", event.auctionId(), event.eventId());
    }

    private void handleAuctionWinnerDeclared(AuctionEventDTO event) {
        AuctionWinnerDeclaredEvent winner = objectMapper.convertValue(event.data(), AuctionWinnerDeclaredEvent.class);
        log.info("AUCTION_WINNER_DECLARED | auctionId={} | eventId={} | winner={} | price={}", event.auctionId(), event.eventId(), winner.winnerUserId(), winner.finalPrice());
    }
}
