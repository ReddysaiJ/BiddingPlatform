package com.example.auction.web.controller;

import com.example.auction.domain.AuctionWatchlistService;
import com.example.auction.domain.PagedResult;
import com.example.auction.domain.models.AuctionResponse;
import com.example.auction.domain.models.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auctions")
public class WatchlistController {
    private static final Logger log = LoggerFactory.getLogger(WatchlistController.class);

    private final AuctionWatchlistService auctionWatchlistService;

    public WatchlistController(AuctionWatchlistService auctionWatchlistService) {
        this.auctionWatchlistService = auctionWatchlistService;
    }

    @GetMapping("/watch")
    public PagedResult<AuctionResponse> myWatchlist(
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @RequestParam(name = "sortBy", defaultValue = "startTime") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction){
        log.info("Fetching watchlist Auctions...");
        return auctionWatchlistService.getAuctions(pageNo, sortBy, direction);
    }

    @PostMapping("/{uid}/watch")
    public ResponseEntity<Void> watch(@PathVariable UUID uid) {
        Customer user = auctionWatchlistService.addToWatchlist(uid);
        log.info("Watchlist updated with {} for user {}", uid, user.id());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{uid}/watch")
    public ResponseEntity<Void> unwatch(@PathVariable UUID uid) {
        Customer user = auctionWatchlistService.removeFromWatchlist(uid);
        log.info("Watchlist deleted with {} for user {}", uid, user.id());
        return ResponseEntity.noContent().build();
    }
}
