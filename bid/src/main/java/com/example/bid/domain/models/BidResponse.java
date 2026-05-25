package com.example.bid.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BidResponse(
        Long bidId,
        UUID auctionId,
        String userId,
        BigDecimal amount,
        LocalDateTime createdAt
) {}
