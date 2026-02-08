package com.example.bid.domain;

import com.example.bid.domain.models.AuctionEventDTO;
import com.example.bid.domain.models.AuctionEventType;
import com.example.bid.domain.models.BidRequest;
import com.example.bid.domain.models.BidResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public class BidMapper {
    static BidRequest toBidRequest(BidEntity entity){
        return new BidRequest(
                entity.getAuctionId(),
                entity.getUserId(),
                entity.getAmount()
        );
    }

    static BidResponse toBidResponse(BidEntity entity){
        return new BidResponse(
                entity.getId(),
                entity.getAuctionId(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getCreatedAt()
        );
    }

    static BidEntity toBidEntity(BidRequest request){
        return new BidEntity(
                request.auctionId(),
                request.userId(),
                request.amount()
        );
    }

    public static PagedResult<BidResponse> toPagedResult(Page<BidResponse> result) {
        return new PagedResult<>(
                result.getContent(),
                result.getTotalElements(),
                result.getNumber(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast(),
                result.hasNext(),
                result.hasPrevious()
        );
    }

    public static AuctionEventDTO buildEvent(AuctionEventType eventType, UUID auctionId, Object data) {
        return new AuctionEventDTO(
                UUID.randomUUID(),
                eventType,
                auctionId,
                java.time.Instant.now(),
                data
        );
    }
}
