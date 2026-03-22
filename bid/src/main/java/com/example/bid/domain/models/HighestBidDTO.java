package com.example.bid.domain.models;

import java.math.BigDecimal;
import java.util.UUID;

public record HighestBidDTO(
        UUID bidId,
        BigDecimal bidAmount,
        String bidderId
) {}