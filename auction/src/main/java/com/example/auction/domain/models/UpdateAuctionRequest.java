package com.example.auction.domain.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateAuctionRequest(
        @NotNull(message = "UID is Required")
        UUID uid,

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Base price is required")
        @Positive(message = "Base price must be greater than zero")
        BigDecimal basePrice,

        @Valid
        AuctionStatus status,

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        LocalDateTime startTime,

        @NotNull(message = "End time is required")
        @Future(message = "End time must be in the future")
        LocalDateTime endTime
) {
}
