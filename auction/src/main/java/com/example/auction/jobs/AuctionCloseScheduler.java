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
public class AuctionCloseScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuctionCloseScheduler.class);

    private final AuctionRepository auctionRepository;
    private final AuctionOutboxEventRepository outboxRepository;

    public AuctionCloseScheduler(AuctionRepository auctionRepository, AuctionOutboxEventRepository outboxRepository) {
        this.auctionRepository = auctionRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    @Scheduled(cron = "0 */1 * * * *")
    public void closeAuctions() {
        List<AuctionEntity> auctions = auctionRepository.findOpenAuctionsPastEndTime();
        if (auctions.isEmpty())
            return;

        for (AuctionEntity auction : auctions) {
            auction.setStatus(AuctionStatus.CLOSED);
            AuctionClosedEvent payload = new AuctionClosedEvent(auction.getUid());
            AuctionEventDTO envelope = AuctionMapper.buildEvent(AuctionEventType.AUCTION_CLOSED, auction.getUid(), payload);

            outboxRepository.save(new AuctionOutboxEventEntity(auction.getUid(), envelope.eventType(), envelope));

            log.info("Auction closed | auctionId={}", auction.getUid());
        }
    }
}
