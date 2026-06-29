package com.example.auction.domain;

import com.example.auction.domain.models.AuctionStatus;
import com.example.auction.domain.models.Customer;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "auctions")
public class AuctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_id_generator")
    @SequenceGenerator(name = "auction_id_generator", sequenceName = "auction_id_seq")
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uid;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "seller_id")),
            @AttributeOverride(name = "name", column = @Column(name = "seller_name")),
            @AttributeOverride(name = "email", column = @Column(name = "seller_email")),
            @AttributeOverride(name = "phone", column = @Column(name = "seller_phone"))
    })
    private Customer seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Column(name = "base_image_url")
    private String baseImageUrl;

    @Column(name = "base_public_id")
    private String basePublicId;

    @Type(JsonType.class)
    @Column(name = "image_urls", columnDefinition = "jsonb")
    private List<String> imageUrls = new ArrayList<>();

    @Type(JsonType.class)
    @Column(name = "image_public_ids", columnDefinition = "jsonb")
    private List<String> imagePublicIds = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(name = "winner_id")
    private String winnerId;

    @Column(name = "winning_amount")
    private BigDecimal winningAmount;

    @Column(name = "winner_declared", nullable = false)
    private boolean winnerDeclared = false;

    protected AuctionEntity() {}

    public AuctionEntity(
            String title,
            String description,
            BigDecimal basePrice,
            Customer seller,
            AuctionStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        this.title = title;
        this.description = description;
        this.basePrice = basePrice;
        this.seller = seller;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void markCompleted(String winnerId, BigDecimal winningAmount) {
        this.status = AuctionStatus.COMPLETED;
        this.winnerId = winnerId;
        this.winningAmount = winningAmount;
        this.winnerDeclared = true;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public String getBaseImageUrl() {
        return baseImageUrl;
    }

    public void setBaseImageUrl(String baseImageUrl) {
        this.baseImageUrl = baseImageUrl;
    }

    public String getBasePublicId() {
        return basePublicId;
    }

    public void setBasePublicId(String basePublicId) {
        this.basePublicId = basePublicId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public void setWinningAmount(BigDecimal winningAmount) {
        this.winningAmount = winningAmount;
    }

    public void setWinnerDeclared(boolean winnerDeclared) {
        this.winnerDeclared = winnerDeclared;
    }

    @PrePersist
    void onCreate() {
        this.uid = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UUID getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public Customer getSeller() {
        return seller;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public List<String> getImagePublicIds() {
        return imagePublicIds;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getWinnerId() {
        return winnerId;
    }
}
