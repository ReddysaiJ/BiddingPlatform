package com.example.bid.domain.models;

import java.math.BigDecimal;

public record DeclareWinnerRequest(
        String winnerUserId,
        BigDecimal finalPrice
) {}

