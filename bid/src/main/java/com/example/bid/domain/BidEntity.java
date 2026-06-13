package com.example.bid.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bids",
        indexes = {
                @Index(name = "idx_bids_auction", columnList = "auction_id"),
                @Index(name = "idx_bids_user", columnList = "user_id"),
                @Index(name = "idx_bids_auction_amount", columnList = "auction_id, amount"),
                @Index(name = "idx_bids_auction_winner", columnList = "auction_id, winner"),
                @Index(name = "idx_bids_auction_final_price", columnList = "auction_id, final_price")
        })
public class BidEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bid_id_generator")
    @SequenceGenerator(name = "bid_id_generator", sequenceName = "bid_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "auction_id", nullable = false, updatable = false)
    private UUID auctionId;

    @Column(name = "auction_title", nullable = false, updatable = false)
    private String auctionTitle;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
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

    public BidEntity(UUID auctionId, String auctionTitle, String userId, BigDecimal amount) {
        this.auctionId = auctionId;
        this.auctionTitle = auctionTitle;
        this.userId = userId;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UUID getAuctionId() {
        return auctionId;
    }

    public String getAuctionTitle() {
        return auctionTitle;
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

