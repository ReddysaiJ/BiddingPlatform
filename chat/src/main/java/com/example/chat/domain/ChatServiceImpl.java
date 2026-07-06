package com.example.chat.domain;

import com.example.chat.domain.exception.ChatAccessDeniedException;
import com.example.chat.domain.exception.ChatRoomNotFoundException;
import com.example.chat.domain.models.ChatMessageResponse;
import com.example.chat.domain.models.ChatRoomResponse;
import com.example.chat.domain.models.PagedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatServiceImpl(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void createRoomIfAbsent(UUID auctionId, String sellerId, String winnerId, BigDecimal finalPrice) {
        if(chatRoomRepository.existsByAuctioId(auctionId)){
            log.info("Chat room already exists for auction={}", auctionId);
            return;
        }
        ChatRoomEntity room = new ChatRoomEntity(auctionId, sellerId, winnerId, finalPrice);
        chatRoomRepository.save(room);
        log.info("Chat room created | auction={} | seller={} | winner={}", auctionId, sellerId, winnerId);
    }

    @Override
    public ChatMessageResponse sendMessage(UUID auctionId, String senderId, String senderName, String content) {
        ChatRoomEntity room = chatRoomRepository.findByAuctionId(auctionId)
                .orElseThrow(() -> new ChatRoomNotFoundException(auctionId));
        assertParticipant(room, senderId);
        ChatMessageEntity msg = new ChatMessageEntity(room, senderId, senderName, content);
        chatMessageRepository.save(msg);

        ChatMessageResponse response = toResponse(msg);
        String destination ="/queue/chat/" + auctionId;
        simpMessagingTemplate.convertAndSendToUser(room.getSellerId(), destination, response);
        simpMessagingTemplate.convertAndSendToUser(room.getWinnerId(), destination, response);

        log.debug("Message sent | auction={} | sender={}", auctionId, senderId);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomResponse getRoom(UUID auctionId, String requesterId) {
        ChatRoomEntity room = chatRoomRepository.findByAuctionId(auctionId)
                .orElseThrow(() -> new ChatRoomNotFoundException(auctionId));
        assertParticipant(room, requesterId);
        return toRoomResponse(room);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<ChatMessageResponse> getHistory(UUID auctionId, String requesterId, int page, int size) {
        ChatRoomEntity room = chatRoomRepository.findByAuctionId(auctionId)
                .orElseThrow(() -> new ChatRoomNotFoundException(auctionId));
        assertParticipant(room, requesterId);

        var paged = chatMessageRepository.findByRoomOrderBySentAtAsc(room, PageRequest.of(page - 1, size));
        return PagedResult.from(paged.map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getMyRooms(String requesterId) {
        return chatRoomRepository.findAll().stream()
                .filter(r -> r.getSellerId().equals(requesterId) || r.getWinnerId().equals(requesterId))
                .map(this::toRoomResponse)
                .toList();
    }

    private void assertParticipant(ChatRoomEntity room, String userId) {
        if (!room.getSellerId().equals(userId) && !room.getWinnerId().equals(userId)) {
            throw new ChatAccessDeniedException();
        }
    }

    private ChatMessageResponse toResponse(ChatMessageEntity msg) {
        return new ChatMessageResponse(
                msg.getId(),
                msg.getRoom().getAuctionId(),
                msg.getSenderId(),
                msg.getSenderName(),
                msg.getContent(),
                msg.getSentAt()
        );
    }

    private ChatRoomResponse toRoomResponse(ChatRoomEntity room) {
        return new ChatRoomResponse(
                room.getId(),
                room.getAuctionId(),
                room.getSellerId(),
                room.getWinnerId(),
                room.getFinalPrice(),
                room.getCreatedAt()
        );
    }
}
