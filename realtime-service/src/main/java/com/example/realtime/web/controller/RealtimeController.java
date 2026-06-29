package com.example.realtime.web.controller;

import com.example.realtime.domain.AuctionRealtimeCache;
import com.example.realtime.domain.models.HighestBidDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/realtime/auctions")
public class RealtimeController {
    private static final Logger log = LoggerFactory.getLogger(RealtimeController.class);

    private final AuctionRealtimeCache cache;

    public RealtimeController(AuctionRealtimeCache cache) {
        this.cache = cache;
    }

    @GetMapping("/{auctionId}/details")
    public Object getAuctionDetails(@PathVariable UUID auctionId) {
        return cache.getAuctionDetails(auctionId);
    }

    @GetMapping("/{auctionId}/status")
    public String getAuctionStatus(@PathVariable UUID auctionId) {
        return cache.getStatus(auctionId);
    }

    @GetMapping("/{auctionId}/highest-bid")
    public HighestBidDTO getHighestBid(@PathVariable UUID auctionId) {
        log.info(String.valueOf(auctionId));
        return cache.getHighestBid(auctionId);
    }

    @GetMapping("/{auctionId}/winner")
    public Object getAuctionWinner(@PathVariable UUID auctionId) {
        return cache.getWinner(auctionId);
    }

    @GetMapping("/{auctionId}")
    public Map<String, Object> getAuctionSummary(@PathVariable UUID auctionId) {
        Map<String, Object> res = new HashMap<>();
        res.put("details", cache.getAuctionDetails(auctionId));
        res.put("status", cache.getStatus(auctionId));
        res.put("highestBid", cache.getHighestBid(auctionId));
        res.put("winner", cache.getWinner(auctionId));

        return res;
    }
}