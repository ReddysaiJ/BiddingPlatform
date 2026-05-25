package com.example.bid.domain.models;

import java.math.BigDecimal;

public record AuctionDTO(
        Long id,
        AuctionStatus status,
        String ownerId,
        BigDecimal currentPrice
) {}

