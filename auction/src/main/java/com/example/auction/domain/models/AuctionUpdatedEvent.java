package com.example.auction.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionUpdatedEvent(
        UUID auctionId,
        String sellerId,
        AuctionStatus status,
        BigDecimal basePrice,
        LocalDateTime updatedAt
) {}

