package com.example.chat.domain.models;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionWinnerDeclaredEvent(
        UUID auctionId,
        String winnerUserId,
        String sellerUserId,
        BigDecimal finalPrice
) {}
