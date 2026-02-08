package com.example.auction.domain;

import com.example.auction.domain.models.AuctionDTO;
import com.example.auction.domain.models.AuctionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuctionRepository extends JpaRepository<AuctionEntity, Long> {

    @Query("""
        select new com.example.auction.domain.models.AuctionResponse(
            a.uid, a.title,
            a.description, a.basePrice,
            a.seller.name, a.status,
            a.startTime, a.endTime,
            a.createdAt, a.updatedAt
        )
        from AuctionEntity a
        where a.uid = :uid
    """)
    Optional<AuctionResponse> findResponseByUid(@Param("uid") UUID uid);

    Optional<AuctionEntity> findByUid(UUID uid);

    Page<AuctionEntity> findBySellerId(Pageable pageable, String sellerId);

    @Query("""
            select new com.example.auction.domain.models.AuctionDTO(
            a.uid,
            a.status,
            a.seller.id,
            a.basePrice
        )
        from AuctionEntity a
        where a.uid = :uid
        """)
    Optional<AuctionDTO> findDTOByUid(UUID uid);

    @Query("""
        select a from AuctionEntity a
        where a.status = 'DRAFT'
          and a.startTime <= CURRENT_TIMESTAMP
    """)
    List<AuctionEntity> findDraftStartedAuctions();

    @Modifying
    @Query("""
        update AuctionEntity a
        set a.status = 'COMPLETED',
            a.winnerId = :winner,
            a.winningAmount = :price
        where a.uid = :auctionId
    """)
    void markCompleted(UUID auctionId, String winner, BigDecimal price);

    @Query("""
        select a from AuctionEntity a
        where a.status = com.example.auction.domain.models.AuctionStatus.OPEN
          and a.endTime <= CURRENT_TIMESTAMP
    """)
    List<AuctionEntity> findOpenAuctionsPastEndTime();
}
