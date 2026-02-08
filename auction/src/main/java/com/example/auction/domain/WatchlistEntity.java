package com.example.auction.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "auction_watchlist",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "auction_id"})
        }
)
class WatchlistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private AuctionEntity auction;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected WatchlistEntity() {}

    WatchlistEntity(String userId, AuctionEntity auction) {
        this.userId = userId;
        this.auction = auction;
    }

    public String getUserId() {
        return userId;
    }

    public AuctionEntity getAuction() {
        return auction;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

