package com.example.realtime.domain;

import com.example.realtime.domain.models.HighestBidDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuctionRealtimeCache {
    private static final Logger log = LoggerFactory.getLogger(AuctionRealtimeCache.class);

    private final RedisTemplate<String, Object> redis;

    public AuctionRealtimeCache(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    private String detailsKey(UUID auctionId) {
        return "auction:" + auctionId + ":details";
    }

    private String statusKey(UUID auctionId) {
        return "auction:" + auctionId + ":status";
    }

    private String highestBidKey(UUID auctionId) {
        return "auction:" + auctionId + ":highestBid";
    }

    private String winnerKey(UUID auctionId) {
        return "auction:" + auctionId + ":winner";
    }

    public void createAuction(UUID auctionId, Object details) {
        redis.opsForValue().set(detailsKey(auctionId), details);
        redis.opsForValue().set(statusKey(auctionId), "CREATED");
    }

    public void updateAuctionDetails(UUID auctionId, Object details) {
        redis.opsForValue().set(detailsKey(auctionId), details);
    }

    public void updateStatus(UUID auctionId, String status) {
        redis.opsForValue().set(statusKey(auctionId), status);
    }

    public void updateHighestBid(UUID auctionId, HighestBidDTO bid) {
        log.info(String.valueOf(auctionId));
        redis.opsForValue().set(highestBidKey(auctionId), bid);
    }

    public void updateWinner(UUID auctionId, Object winner) {
        redis.opsForValue().set(winnerKey(auctionId), winner);
    }

    public void deleteAuction(UUID auctionId) {
        redis.delete(detailsKey(auctionId));
        redis.delete(statusKey(auctionId));
        redis.delete(highestBidKey(auctionId));
        redis.delete(winnerKey(auctionId));
    }

    public Object getAuctionDetails(UUID auctionId) {
        return redis.opsForValue().get(detailsKey(auctionId));
    }

    public String getStatus(UUID auctionId) {
        return (String) redis.opsForValue().get(statusKey(auctionId));
    }

    public HighestBidDTO getHighestBid(UUID auctionId) {
        log.info(String.valueOf(auctionId));
        return (HighestBidDTO) redis.opsForValue().get(highestBidKey(auctionId));
    }

    public Object getWinner(UUID auctionId) {
        return redis.opsForValue().get(winnerKey(auctionId));
    }
}