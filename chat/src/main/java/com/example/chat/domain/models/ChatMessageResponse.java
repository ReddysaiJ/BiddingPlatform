package com.example.chat.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatMessageResponse(
        Long id,
        UUID auctionId,
        String senderId,
        String senderName,
        String content,
        LocalDateTime sentAt
) {}
