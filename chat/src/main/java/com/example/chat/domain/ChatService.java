package com.example.chat.domain;

import com.example.chat.domain.models.ChatMessageResponse;
import com.example.chat.domain.models.ChatRoomResponse;
import com.example.chat.domain.models.PagedResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ChatService {
    public void createRoomIfAbsent(UUID auctionId, String sellerId, String winnerId, BigDecimal finalPrice);

    public ChatMessageResponse sendMessage(UUID auctionId, String senderId, String senderName, String content);

    public ChatRoomResponse getRoom(UUID auctionId, String requesterId);

    public PagedResult<ChatMessageResponse> getHistory(UUID auctionId, String requesterId, int page, int size);

    public List<ChatRoomResponse> getMyRooms(String requesterId);
}
