package com.example.bid.domain;

import com.example.bid.domain.models.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BidOutboxRepository extends JpaRepository<BidOutboxEventEntity, UUID> {

    List<BidOutboxEventEntity> findTop100ByStatusOrderByCreatedAt(OutboxStatus status);
}
