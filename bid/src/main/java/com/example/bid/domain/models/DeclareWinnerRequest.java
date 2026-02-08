package com.example.bid.domain.models;

import java.math.BigDecimal;
import java.util.UUID;

public record DeclareWinnerRequest(
        String winnerUserId,
        BigDecimal finalPrice
) {}

