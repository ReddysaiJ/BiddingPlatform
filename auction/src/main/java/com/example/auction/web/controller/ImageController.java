package com.example.auction.web.controller;

import com.example.auction.domain.AuctionService;
import com.example.auction.domain.CloudinaryService;
import com.example.auction.domain.models.AuctionImageResponse;
import com.example.auction.domain.models.SaveImageUrlsRequest;
import com.example.auction.domain.models.SignedUploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auctions/images")
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "http://127.0.0.1:4200")
public class ImageController {

    private final CloudinaryService cloudinaryService;
    private final AuctionService auctionService;

    public ImageController(CloudinaryService cloudinaryService,
                           AuctionService auctionService) {
        this.cloudinaryService = cloudinaryService;
        this.auctionService = auctionService;
    }

    @GetMapping("/sign/base")
    public ResponseEntity<SignedUploadResponse> signBase() {
        return ResponseEntity.ok(cloudinaryService.generateSignedUrl());
    }

    @GetMapping("/{uid}/sign")
    public ResponseEntity<List<SignedUploadResponse>> signExtra(
            @PathVariable UUID uid,
            @RequestParam(defaultValue = "1") int count) {
        return ResponseEntity.ok(cloudinaryService.generateSignedUrls(uid, count));
    }

    @GetMapping("/{uid}")
    public ResponseEntity<AuctionImageResponse> getImages(@PathVariable UUID uid) {
        return ResponseEntity.ok(auctionService.getAuctionImages(uid));
    }

    @PatchMapping("/{uid}")
    public ResponseEntity<Void> saveImageUrls(
            @PathVariable UUID uid,
            @RequestBody SaveImageUrlsRequest request) {
        auctionService.saveImageUrls(uid, request.imageUrls(), request.publicIds());
        return ResponseEntity.noContent().build();
    }
}