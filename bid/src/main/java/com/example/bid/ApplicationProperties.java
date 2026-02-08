package com.example.bid;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "bid")
public record ApplicationProperties(
        @DefaultValue("10")
        @Min(1)
        int pageSize,
        String auctionExchange) {
}
