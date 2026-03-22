package com.example.auction.jobs;

import com.example.auction.domain.*;
import com.example.auction.domain.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@EnableScheduling
public class AuctionOpenScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuctionOpenScheduler.class);

    private final AuctionRepository auctionRepository;
    private final AuctionOutboxEventRepository outboxRepository;

    public AuctionOpenScheduler(AuctionRepository auctionRepository,
                                AuctionOutboxEventRepository outboxRepository) {
        this.auctionRepository = auctionRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    @Scheduled(cron = "*/10 * * * * *")
    public void openAuctions() {

        List<AuctionEntity> auctions = auctionRepository.findDraftStartedAuctions();

        if (auctions.isEmpty())
            return;

        for (AuctionEntity auction : auctions) {
            try {
                auction.setStatus(AuctionStatus.OPEN);
                AuctionOpenEvent payload = new AuctionOpenEvent(auction.getUid(), auction.getSeller().id());
                AuctionEventDTO event = AuctionMapper.buildEvent(AuctionEventType.AUCTION_OPEN, auction.getUid(), payload);

                outboxRepository.save(new AuctionOutboxEventEntity(auction.getUid(), event.eventType(), event));
                log.info("Auction opened: {}", auction.getUid());

            } catch (Exception ex) {
                log.error("Failed to open auction {}", auction.getUid(), ex);
            }
        }
    }
}