package com.example.auction.domain.models;

import java.time.Instant;
import java.util.UUID;

public record AuctionEventDTO(
        UUID eventId,
        AuctionEventType eventType,
        UUID auctionId,
        Instant occurredAt,
        Object data
) {}
