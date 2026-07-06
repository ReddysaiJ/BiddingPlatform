package com.example.chat;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chat")
public record ApplicationProperties(
        String auctionExchange
) {}
