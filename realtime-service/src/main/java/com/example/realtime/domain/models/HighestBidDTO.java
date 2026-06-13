package com.example.realtime.domain.models;

import java.math.BigDecimal;

public record HighestBidDTO(
        Long bidId,
        BigDecimal bidAmount,
        String bidderId
) {}