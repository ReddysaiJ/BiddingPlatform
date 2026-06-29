package com.example.auction.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AuctionCacheResponse(
        UUID uid,
        String title,
        String description,
        BigDecimal basePrice,
        String seller,
        AuctionStatus status,
        String baseImageUrl,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
