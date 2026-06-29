package com.example.auction.domain.models;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateAuctionRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Base price is required")
        @Positive(message = "Base price must be greater than zero")
        BigDecimal basePrice,

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        LocalDateTime startTime,

        @NotNull(message = "End time is required")
        @Future(message = "End time must be in the future")
        LocalDateTime endTime,

        String baseImageUrl,

        String basePublicId
) {
}
