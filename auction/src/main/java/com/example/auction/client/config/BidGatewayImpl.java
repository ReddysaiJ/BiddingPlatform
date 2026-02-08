//package com.example.auction.client.config;
//
//import com.example.auction.domain.models.BidResponse;
//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
//import io.github.resilience4j.retry.annotation.Retry;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//@Service
//public class BidGatewayImpl implements BidGateway {
//
//    private static final Logger log = LoggerFactory.getLogger(BidGatewayImpl.class);
//
//    private final BidClient bidClient;
//
//    public BidGatewayImpl(BidClient bidClient) {
//        this.bidClient = bidClient;
//    }
//
//    @Override
//    @CircuitBreaker(name = "bidService", fallbackMethod = "getHighestBidFallback")
//    @Retry(name = "bidService")
//    public BidResponse getHighestBid(UUID auctionId) {
//        log.info("Fetching highest bid | auctionId={}", auctionId);
//        return bidClient.getHighestBid(auctionId);
//    }
//
//    public BidResponse getHighestBidFallback(UUID auctionId, Throwable ex) {
//        log.error("Bid service unavailable | auctionId={}", auctionId, ex);
//        throw new ServiceUnavailableException("Bid service unavailable while fetching highest bid");
//    }
//}
