package com.example.bid.clients.config;

import java.math.BigDecimal;
import java.util.UUID;

public interface AuctionGateway {

    void declareWinner(UUID auctionId, String winnerUserId, BigDecimal finalPrice);
}

