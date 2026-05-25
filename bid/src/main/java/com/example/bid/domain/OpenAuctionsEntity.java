package com.example.bid.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "open_auctions")
public class OpenAuctionsEntity {

    @Id
    @Column(name = "auction_uid")
    private UUID auctionUid;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "highest_bid", precision = 12, scale = 2)
    private BigDecimal highestBid;

    @Column(name = "highest_bidder_id")
    private String highestBidderId;

    @Column(name = "bid_count", nullable = false)
    private Integer bidCount;

    public OpenAuctionsEntity() {}

    public OpenAuctionsEntity(UUID auctionUid, String userId) {
        this.auctionUid = auctionUid;
        this.userId = userId;
    }

    public UUID getAuctionUid() {
        return auctionUid;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(BigDecimal highestBid) {
        this.highestBid = highestBid;
    }

    public String getHighestBidderId() {
        return highestBidderId;
    }

    public void setHighestBidderId(String highestBidderId) {
        this.highestBidderId = highestBidderId;
    }

    public Integer getBidCount() {
        return bidCount;
    }

    public void setBidCount(Integer bidCount) {
        this.bidCount = bidCount;
    }
}