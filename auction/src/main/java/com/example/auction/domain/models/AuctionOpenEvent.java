package com.example.auction.domain.models;

import java.util.UUID;

public record AuctionOpenEvent(
        UUID auctionUid,
        String userId
) {
}
