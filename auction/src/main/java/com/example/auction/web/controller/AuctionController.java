package com.example.auction.web.controller;

import com.example.auction.domain.AuctionService;
import com.example.auction.domain.PagedResult;
import com.example.auction.domain.UserService;
import com.example.auction.domain.models.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {
    private static final Logger log = LoggerFactory.getLogger(AuctionController.class);

    private final AuctionService auctionService;
    private final UserService userService;

    public AuctionController(AuctionService auctionService, UserService userService) {
        this.auctionService = auctionService;
        this.userService = userService;
    }

    @GetMapping
    public PagedResult<AuctionResponse> openAuctions(@RequestParam(name = "page", defaultValue = "1") int pageNo,
                                                @RequestParam(name = "sortBy", defaultValue = "startTime") String sortBy,
                                                @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                @RequestParam(name = "query", defaultValue = "") String query){
        log.info("Fetching Auctions...");
        return auctionService.getAuctions(pageNo, sortBy, direction, query);
    }

    @GetMapping("/my")
    public PagedResult<AuctionResponse> myAuctions(@RequestParam(name = "page", defaultValue = "1") int pageNo,
                                              @RequestParam(name = "sortBy", defaultValue = "startTime") String sortBy,
                                              @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                              @RequestParam(name = "query", defaultValue = "") String query){
        Customer user = userService.getSeller();
        log.info("Fetching Auctions for user : {}", user.id());
        return auctionService.getAuctions(pageNo, sortBy, direction, query, user.id());
    }

    @GetMapping("/{uid}")
    public AuctionResponse open(@PathVariable UUID uid) {
        return auctionService.getAuctionResponse(uid);
    }

//    @GetMapping("/u/{uid}")
//    AuctionDTO openBy(@PathVariable UUID uid) {
//        return auctionService.getAuctionDTO(uid);
//    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(
            @Valid @RequestBody CreateAuctionRequest request
    ) {
        AuctionResponse created = auctionService.create(request);
        URI location = URI.create("/api/auctions/" + created.uid());
        log.info("Creating auction ");
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping
    public ResponseEntity<AuctionResponse> updateAuction(@Valid @RequestBody UpdateAuctionRequest request) {
        AuctionResponse updated = auctionService.update(request);
        URI location = URI.create("/api/auctions/" + updated.uid());
        log.info("Updating auction uid: {}", updated.uid());
        return ResponseEntity.created(location).body(updated);
    }

    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> deleteAuction(@PathVariable UUID uid){
        auctionService.delete(uid);
        log.info("Deleting auction uid: {}", uid);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/{auctionId}/winner")
//    void declareWinner(@PathVariable UUID auctionId, @RequestBody DeclareWinnerRequest request) {
//        auctionService.declareWinner(
//                auctionId,
//                request.winnerUserId(),
//                request.finalPrice()
//        );
//    }

//    @GetMapping("/me")
//    public Authentication me(Authentication auth) {
//        return auth;
//    }
}
