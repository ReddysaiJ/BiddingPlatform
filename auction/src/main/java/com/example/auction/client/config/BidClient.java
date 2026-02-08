//package com.example.auction.client.config;
//
//import com.example.auction.domain.models.BidResponse;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.UUID;
//
//@FeignClient(
//        name = "BID",
//        configuration = BidFeignErrorDecoder.class
//)
//public interface BidClient {
//
//    @GetMapping("/api/bids/auction/{auctionId}/highest")
//    BidResponse getHighestBid(@PathVariable UUID auctionId);
//}
