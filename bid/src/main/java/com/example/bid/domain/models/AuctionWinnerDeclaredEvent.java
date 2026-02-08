package com.example.bid.domain.models;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionWinnerDeclaredEvent(
        UUID auctionId,
        String winnerUserId,
        BigDecimal finalPrice
) {}