package com.example.chat.domain.models;

import java.util.Map;
import java.util.UUID;

public record AuctionEventDTO(
        UUID eventId,
        UUID auctionId,
        AuctionEventType eventType,
        Map<String, Object> data
) {}
