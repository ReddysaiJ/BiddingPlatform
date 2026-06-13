package com.example.bid.domain.models;

import java.util.UUID;

public record BidResponseAuction(
        UUID auctionUid,
        String auctionTitle,
        Long totalBids
) {
}