package com.example.bid.domain;

import com.example.bid.domain.models.OutboxStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_event_entity")
public class ProcessedEventEntity {

    @Id
    private UUID eventId;
    private LocalDateTime processedAt;
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

