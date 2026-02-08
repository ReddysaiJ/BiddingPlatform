package com.example.bid.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bids",
        indexes = {
                @Index(name = "idx_bids_auction", columnList = "auction_id"),
                @Index(name = "idx_bids_user", columnList = "user_id")
        })
public class BidEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "auction_id", nullable = false)
    private UUID auctionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "winner", nullable = false)
    private boolean winner = false;

    @Column(name = "final_price", precision = 12, scale = 2)
    private BigDecimal finalPrice;

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    protected BidEntity() {}

    public BidEntity(UUID auctionId, String userId, BigDecimal amount) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getAuctionId() {
        return auctionId;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

