package com.example.bid.clients.config;

import com.example.bid.domain.models.DeclareWinnerRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AuctionGatewayImpl implements AuctionGateway {

    private static final Logger log = LoggerFactory.getLogger(AuctionGatewayImpl.class);

    private final AuctionClient auctionClient;

    public AuctionGatewayImpl(AuctionClient auctionClient) {
        this.auctionClient = auctionClient;
    }

    @Override
    @CircuitBreaker(name = "auctionService")
    @Retry(name = "auctionService", fallbackMethod = "declareWinnerFallback")
    public void declareWinner(UUID auctionId, String winnerUserId, BigDecimal finalPrice) {
        log.info("Declaring winner | auctionId={} | user={} | amount={}", auctionId, winnerUserId, finalPrice);
        auctionClient.declareWinner(auctionId, new DeclareWinnerRequest(winnerUserId, finalPrice));
    }

    public void declareWinnerFallback(UUID auctionId, String winnerUserId, BigDecimal finalPrice, Throwable ex) {
        log.error("Auction service unavailable | auctionId={} | winner={}", auctionId, winnerUserId, ex);
        throw ex instanceof RuntimeException re ? re : new RuntimeException(ex);
    }
}

