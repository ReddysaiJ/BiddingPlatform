package com.example.bid.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "open_auctions")
public class OpenAuctionsEntity {
    @Id
    private UUID auctionUid;

    @Column(name = "user_id", nullable = false)
    private String userId;

    public OpenAuctionsEntity() {}

    public OpenAuctionsEntity(UUID auctionUid, String userId) {
        this.auctionUid = auctionUid;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
