package com.example.chat.web.controller;

import com.example.chat.domain.ChatService;
import com.example.chat.domain.models.SendMessageRequest;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWebSocketController {

    private final ChatService chatService;

    public ChatWebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.send")
    public void send(@Valid @Payload SendMessageRequest request, Principal principal) {
        JwtAuthenticationToken auth = (JwtAuthenticationToken) principal;
        Jwt jwt = (Jwt) auth.getCredentials();

        String senderId   = jwt.getSubject();
        String senderName = jwt.getClaimAsString("preferred_username");

        chatService.sendMessage(request.auctionId(), senderId, senderName, request.content());
    }
}
