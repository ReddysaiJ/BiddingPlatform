package com.example.bid.domain.models;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionDTO(
        UUID id,
        AuctionStatus status,
        String ownerId,
        BigDecimal currentPrice
) {}

