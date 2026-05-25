package com.example.bid.job;

import com.example.bid.domain.BidOutboxEventEntity;
import com.example.bid.domain.BidOutboxRepository;
import com.example.bid.domain.models.OutboxStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BidOutboxPublisherJob {

    private final BidOutboxRepository outboxRepository;
    private final BidEventPublisher publisher;

    public BidOutboxPublisherJob(
            BidOutboxRepository outboxRepository,
            BidEventPublisher publisher
    ) {
        this.outboxRepository = outboxRepository;
        this.publisher = publisher;
    }

    @Scheduled(cron = "*/2 * * * * *")
    @Transactional
    public void publishEvents() {
        List<BidOutboxEventEntity> events = outboxRepository.findTop100ByStatusOrderByCreatedAt(OutboxStatus.NEW);

        for (BidOutboxEventEntity event : events) {
            try {
                publisher.publish(event);
                event.setStatus(OutboxStatus.SENT);
            } catch (Exception e) {
                event.setStatus(OutboxStatus.FAILED);
            }
        }
    }
}