package com.example.bid.job;

import com.example.bid.domain.*;
import com.example.bid.domain.models.*;
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
    private final BidOutboxRepository bidOutboxRepository;
    private final ObjectMapper objectMapper;
    private final ProcessedEventRepository processedEventRepository;

    public BidAuctionEventHandlerService(
            OpenAuctionsRepository openAuctionsRepository,
            BidRepository bidRepository, BidOutboxRepository bidOutboxRepository,
            ObjectMapper objectMapper,
            ProcessedEventRepository processedEventRepository) {
        this.openAuctionsRepository = openAuctionsRepository;
        this.bidRepository = bidRepository;
        this.bidOutboxRepository = bidOutboxRepository;
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
            case AUCTION_CLOSED -> handleAuctionClosed(event);
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

    private void handleAuctionClosed(AuctionEventDTO event) {
        openAuctionsRepository.deleteById(event.auctionId());

        bidRepository.findHighestBid(event.auctionId()).ifPresentOrElse(bid -> {
            bidRepository.markWinner(bid.getId());

            AuctionWinnerDeclaredEvent winnerPayload = new AuctionWinnerDeclaredEvent(
                    bid.getAuctionId(),
                    bid.getUserId(),
                    bid.getAmount()
            );
            bidOutboxRepository.save(new BidOutboxEventEntity(
                    event.auctionId(),
                    AuctionEventType.AUCTION_WINNER_DECLARED,
                    winnerPayload,
                    OutboxStatus.NEW
            ));

            log.info("AUCTION_CLOSED – winner queued for declaration | auctionId={} | winner={} | price={}",
                    event.auctionId(), bid.getUserId(), bid.getAmount());

        }, () -> log.warn("AUCTION_CLOSED – no bids, no winner | auctionId={}", event.auctionId()));
    }

    private void handleAuctionWinnerDeclared(AuctionEventDTO event) {
        AuctionWinnerDeclaredEvent winner = objectMapper.convertValue(event.data(), AuctionWinnerDeclaredEvent.class);
        log.info("AUCTION_WINNER_DECLARED | auctionId={} | eventId={} | winner={} | price={}", event.auctionId(), event.eventId(), winner.winnerUserId(), winner.finalPrice());
    }
}
