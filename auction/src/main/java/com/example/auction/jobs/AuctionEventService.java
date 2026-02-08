package com.example.auction.jobs;

import com.example.auction.domain.AuctionEventPublisher;
import com.example.auction.domain.AuctionOutboxEventEntity;
import com.example.auction.domain.AuctionOutboxEventRepository;
import com.example.auction.domain.models.OutboxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
class AuctionEventService {

    private static final Logger log = LoggerFactory.getLogger(AuctionEventService.class);

    private final AuctionOutboxEventRepository repository;
    private final AuctionEventPublisher publisher;

    AuctionEventService(AuctionOutboxEventRepository repository, AuctionEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public void publishAuctionEvents() {
        List<AuctionOutboxEventEntity> events = repository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);

        for (AuctionOutboxEventEntity event : events) {
            try {
                publisher.publish(event);
                event.setStatus(OutboxStatus.SENT);
                event.setUpdatedAt(LocalDateTime.now());

            } catch (Exception ex) {
                event.setRetryCount(event.getRetryCount() + 1);
                event.setUpdatedAt(LocalDateTime.now());
                if (event.getRetryCount() >= 5) {
                    event.setStatus(OutboxStatus.FAILED);
                }
                log.error("Outbox publish failed | eventId={} | type={} | retries={}", event.getEventId(), event.getEventType(),
                        event.getRetryCount(), ex
                );
            }
        }
    }
}

