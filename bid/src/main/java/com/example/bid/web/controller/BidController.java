package com.example.bid.web.controller;

import com.example.bid.domain.BidService;
import com.example.bid.domain.PagedResult;
import com.example.bid.domain.models.BidRequest;
import com.example.bid.domain.models.BidResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bids")
public class BidController {
    private static final Logger log = LoggerFactory.getLogger(BidController.class);

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    public ResponseEntity<BidResponse> placeBid(@RequestBody BidRequest request) {
        log.info("Going to place a bid for user : {}", request.userId());
        return ResponseEntity.ok(bidService.placeBid(request));
    }

    @GetMapping("/auction/{auctionId}")
    public PagedResult<BidResponse> getBidsByAuction(
            @PathVariable UUID auctionId,
            @RequestParam(name = "page", defaultValue = "1") int pageNo) {
        log.info("Going to fetch Bids by AuctionId : {}", auctionId);
        return bidService.getBidsByAuction(auctionId, pageNo);
    }

    @GetMapping("/user/{userId}")
    public PagedResult<BidResponse> getBidsByUser(
            @PathVariable String userId,
            @RequestParam(name = "page", defaultValue = "1") int pageNo) {
        log.info("Going to fetch Bids by UserId : {}", userId);
        return bidService.getBidsByUser(userId, pageNo);
    }

    @GetMapping("/auction/{auctionId}/highest")
    public ResponseEntity<BidResponse> getHighestBid(@PathVariable UUID auctionId) {
        log.info("Going to fetch Highest Bid by AuctionId : {}", auctionId);
        return ResponseEntity.ok(bidService.getHighestBid(auctionId));
    }
}
