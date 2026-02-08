package com.example.auction.jobs;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
class AuctionEventsPublishingJob {

    private static final Logger log = LoggerFactory.getLogger(AuctionEventsPublishingJob.class);

    private final AuctionEventService auctionEventService;

    AuctionEventsPublishingJob(AuctionEventService auctionEventService) {
        this.auctionEventService = auctionEventService;
    }

    @Scheduled(cron = "${auction.publish-auction-event-job-cron}")
    @SchedulerLock(name = "publishAuctionEvents")
    public void publishAuctionEvents() {
        LockAssert.assertLocked();
        log.info("Publishing Auction Events at {}", Instant.now());
        auctionEventService.publishAuctionEvents();
    }
}

