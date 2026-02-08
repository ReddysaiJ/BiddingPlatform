package com.example.auction.domain;

import com.example.auction.domain.models.OutboxStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface AuctionOutboxEventRepository extends JpaRepository<AuctionOutboxEventEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<AuctionOutboxEventEntity> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
