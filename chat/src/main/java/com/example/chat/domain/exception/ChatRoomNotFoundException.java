package com.example.chat.domain.exception;

import java.util.UUID;

public class ChatRoomNotFoundException extends RuntimeException {
    public ChatRoomNotFoundException(UUID auctionId) {
        super("Chat room not found for auction: " + auctionId);
    }
}
