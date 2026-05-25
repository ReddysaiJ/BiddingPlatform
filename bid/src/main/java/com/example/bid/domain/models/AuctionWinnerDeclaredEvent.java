package com.example.bid.domain.models;

import java.math.BigDecimal;

public record AuctionWinnerDeclaredEvent(
        Long auctionId,
        String winnerUserId,
        BigDecimal finalPrice
) {}