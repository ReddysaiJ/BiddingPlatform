package com.example.chat.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChatRoomResponse(
        Long id,
        UUID auctionId,
        String sellerId,
        String winnerId,
        BigDecimal finalPrice,
        LocalDateTime createdAt
) {}
