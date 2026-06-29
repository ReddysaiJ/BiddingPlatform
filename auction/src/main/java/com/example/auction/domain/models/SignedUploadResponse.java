package com.example.auction.domain.models;

public record SignedUploadResponse(
        String signature,
        String apiKey,
        long timestamp,
        String cloudName,
        String folder,
        String publicId,
        String uploadUrl
) {}
