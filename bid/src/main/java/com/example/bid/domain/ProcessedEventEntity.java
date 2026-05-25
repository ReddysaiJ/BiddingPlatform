package com.example.bid.domain;

import com.example.bid.domain.models.OutboxStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_event_entity")
public class ProcessedEventEntity {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OutboxStatus status;

    protected ProcessedEventEntity() {}

    public ProcessedEventEntity(UUID eventId, OutboxStatus status) {
        this.eventId = eventId;
        this.processedAt = LocalDateTime.now();
        this.status = status;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    public UUID getEventId() {
        return eventId;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}

