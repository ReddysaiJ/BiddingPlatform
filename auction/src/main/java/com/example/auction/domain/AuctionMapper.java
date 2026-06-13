package com.example.auction.domain;

import com.example.auction.domain.models.*;

import java.util.UUID;

public class AuctionMapper {
    static AuctionResponse toAuctionDTO(AuctionEntity auctionEntity, boolean watch){
        return new AuctionResponse(
                auctionEntity.getUid(),
                auctionEntity.getTitle(),
                auctionEntity.getDescription(),
                auctionEntity.getBasePrice(),
                auctionEntity.getSeller().name(),
                auctionEntity.getStatus(),
                auctionEntity.getStartTime(),
                auctionEntity.getEndTime(),
                watch,
                auctionEntity.getCreatedAt(),
                auctionEntity.getUpdatedAt()
        );
    }

    static AuctionCreatedEvent toAuctionCreatedEvent(AuctionEntity auctionEntity, Customer seller){
        return new AuctionCreatedEvent(
                auctionEntity.getUid(),
                seller.id(),
                auctionEntity.getTitle(),
                auctionEntity.getBasePrice(),
                auctionEntity.getStartTime(),
                auctionEntity.getEndTime()
        );
    }

    static AuctionUpdatedEvent toAuctionUpdatedEvent(AuctionEntity auctionEntity, Customer seller){
        return new AuctionUpdatedEvent(
                auctionEntity.getUid(),
                seller.id(),
                auctionEntity.getStatus(),
                auctionEntity.getBasePrice(),
                auctionEntity.getUpdatedAt()
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
