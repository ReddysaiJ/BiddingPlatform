package com.example.auction.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionCreatedEvent(
        UUID auctionId,
        String sellerId,
        String title,
        BigDecimal basePrice,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}
