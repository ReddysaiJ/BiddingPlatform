package com.example.chat.web.controller;

import com.example.chat.domain.ChatService;
import com.example.chat.domain.models.ChatMessageResponse;
import com.example.chat.domain.models.ChatRoomResponse;
import com.example.chat.domain.models.PagedResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> myRooms(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(chatService.getMyRooms(jwt.getSubject()));
    }

    @GetMapping("/rooms/{auctionId}")
    public ResponseEntity<ChatRoomResponse> getRoom(
            @PathVariable UUID auctionId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(chatService.getRoom(auctionId, jwt.getSubject()));
    }

    @GetMapping("/rooms/{auctionId}/messages")
    public ResponseEntity<PagedResult<ChatMessageResponse>> getHistory(
            @PathVariable UUID auctionId,
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                chatService.getHistory(auctionId, jwt.getSubject(), page, size));
    }
}
