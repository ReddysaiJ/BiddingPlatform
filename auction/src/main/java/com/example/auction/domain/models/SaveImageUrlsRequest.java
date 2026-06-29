package com.example.auction.domain.models;

import java.util.List;

public record SaveImageUrlsRequest(
        List<String> imageUrls,
        List<String> publicIds
) {
}
