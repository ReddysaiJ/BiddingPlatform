package com.example.bid.domain.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record AuctionDataDTO(
        String title,
        String sellerId,
        String auctionId,
        double basePrice,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        LocalDateTime startTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        LocalDateTime endTime
) {}
