package com.example.auction.domain.models;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionClosedEvent(
        UUID auctionUid
) {}

