package com.example.auction.domain.models;

import java.util.List;
import java.util.UUID;

public record AuctionImageResponse(
        UUID auctionUid,
        String baseImageUrl,
        List<String> imageUrls
) {
}
