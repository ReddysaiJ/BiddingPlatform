package com.example.bid.domain.models;

import java.time.Instant;
import java.util.UUID;

public record AuctionEventDTO(
        UUID eventId,
        AuctionEventType eventType,
        UUID auctionId,
        Instant occurredAt,
        Object data
) {}