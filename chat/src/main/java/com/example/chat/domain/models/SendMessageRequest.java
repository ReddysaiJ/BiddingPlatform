package com.example.chat.domain.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SendMessageRequest(
        @NotBlank UUID auctionId,
        @NotBlank @Size(min = 1, max = 2000) String content
) {}
