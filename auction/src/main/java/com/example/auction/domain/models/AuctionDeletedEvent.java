package com.example.auction.domain.models;

import java.util.UUID;

public record AuctionDeletedEvent(
        UUID auctionId,
        String sellerId
) {}

