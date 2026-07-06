package com.example.chat.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_rooms")
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_room_id_gen")
    @SequenceGenerator(name = "chat_room_id_gen", sequenceName = "chat_room_id_seq")
    private Long id;

    @Column(name = "auction_id", nullable = false, unique = true, updatable = false)
    private UUID auctionId;

    @Column(name = "seller_id", nullable = false, updatable = false)
    private String sellerId;

    @Column(name = "winner_id", nullable = false, updatable = false)
    private String winnerId;

    @Column(name = "final_price", nullable = false, updatable = false)
    private BigDecimal finalPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ChatRoomEntity() {}

    public ChatRoomEntity(UUID auctionId, String sellerId, String winnerId, BigDecimal finalPrice) {
        this.auctionId = auctionId;
        this.sellerId  = sellerId;
        this.winnerId  = winnerId;
        this.finalPrice = finalPrice;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UUID getAuctionId() {
        return auctionId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
