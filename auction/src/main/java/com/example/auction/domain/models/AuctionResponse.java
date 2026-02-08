package com.example.auction.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionResponse(
        UUID uid,
        String title,
        String description,
        BigDecimal basePrice,

        String seller,

        AuctionStatus status,
        LocalDateTime startTime,
        LocalDateTime endTime,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
