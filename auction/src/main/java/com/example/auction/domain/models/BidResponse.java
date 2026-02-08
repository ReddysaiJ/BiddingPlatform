package com.example.auction.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BidResponse(
        UUID bidId,
        UUID auctionId,
        String userId,
        BigDecimal amount,
        LocalDateTime createdAt
) {}
