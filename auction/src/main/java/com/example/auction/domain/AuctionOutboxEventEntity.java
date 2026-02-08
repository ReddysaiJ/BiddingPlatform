package com.example.auction.domain;

import com.example.auction.domain.models.AuctionEventDTO;
import com.example.auction.domain.models.AuctionEventType;
import com.example.auction.domain.models.OutboxStatus;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auction_event_outbox")
public class AuctionOutboxEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_event_id_generator")
    @SequenceGenerator(name = "auction_event_id_generator", sequenceName = "auction_event_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private UUID auctionUid;

    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionEventType eventType;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private AuctionEventDTO payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private int retryCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected AuctionOutboxEventEntity() {}

    public AuctionOutboxEventEntity(UUID auctionUid, AuctionEventType eventType, AuctionEventDTO payload) {
        this.auctionUid = auctionUid;
        this.eventType = eventType;
        this.payload = payload;
        this.status = OutboxStatus.NEW;
        this.retryCount = 0;
    }

    @PrePersist
    void onCreate() {
        this.eventId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

    public UUID getAuctionUid() {
        return auctionUid;
    }

    public void setAuctionUid(UUID auctionUid) {
        this.auctionUid = auctionUid;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public AuctionEventType getEventType() {
        return eventType;
    }

    public void setEventType(AuctionEventType eventType) {
        this.eventType = eventType;
    }

    public AuctionEventDTO getPayload() {
        return payload;
    }

    public void setPayload(AuctionEventDTO payload) {
        this.payload = payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
