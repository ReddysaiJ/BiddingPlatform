package com.example.bid.clients.config;

import com.example.bid.domain.models.AuctionDTO;
import com.example.bid.domain.models.DeclareWinnerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "AUCTION", configuration = AuctionFeignErrorDecoder.class)
public interface AuctionClient {

    @PostMapping("/api/auctions/{auctionId}/winner")
    void declareWinner(@PathVariable UUID auctionId, @RequestBody DeclareWinnerRequest request);
}
