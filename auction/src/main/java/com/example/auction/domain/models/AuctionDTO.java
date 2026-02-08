package com.example.auction.domain.models;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionDTO(
        UUID uid,
        AuctionStatus status,
        String ownerId,
        BigDecimal currentPrice
) {}

