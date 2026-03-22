package com.example.bid.domain;

import com.example.bid.domain.models.AuctionEventType;
import com.example.bid.domain.models.HighestBidDTO;
import com.example.bid.domain.models.OutboxStatus;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bid_outbox_events")
public class BidOutboxEventEntity {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "auction_id", nullable = false)
    private UUID auctionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private AuctionEventType eventType;

    @Type(JsonBinaryType.class)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private HighestBidDTO payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public BidOutboxEventEntity() {}

    public BidOutboxEventEntity(
            UUID auctionId,
            AuctionEventType eventType,
            HighestBidDTO payload,
            OutboxStatus status
    ) {
        this.eventId = UUID.randomUUID();
        this.auctionId = auctionId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getAuctionId() {
        return auctionId;
    }

    public AuctionEventType getEventType() {
        return eventType;
    }

    public HighestBidDTO getPayload() {
        return payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}