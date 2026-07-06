package com.example.chat.domain.exception;

public class ChatAccessDeniedException extends RuntimeException {
    public ChatAccessDeniedException() {
        super("You are not a participant of this chat room.");
    }
}
