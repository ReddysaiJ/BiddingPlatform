package com.example.bid.domain.models;

import java.math.BigDecimal;
import java.util.UUID;

public record BidRequest(
        UUID auctionId,
        String userId,
        BigDecimal amount
) {}

