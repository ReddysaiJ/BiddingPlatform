package com.example.auction.domain;

import com.example.auction.domain.models.AuctionCacheResponse;
import com.example.auction.domain.models.AuctionStatus;
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
    // ── Single auction lookup ─────────────────────────────────────────────────
    @Query("""
        SELECT new com.example.auction.domain.models.AuctionCacheResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.baseImageUrl,
            a.startTime,
            a.endTime,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        WHERE a.uid = :uid
    """)
    Optional<AuctionCacheResponse> findCacheResponseByUid(@Param("uid") UUID uid);

    Optional<AuctionEntity> findByUid(UUID uid);

    // ── Paginated list — all auctions by status ───────────────────────────────
    @Query("""
        SELECT new com.example.auction.domain.models.AuctionCacheResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.baseImageUrl,
            a.startTime,
            a.endTime,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        WHERE (:status IS NULL OR a.status = :status)
    """)
    Page<AuctionCacheResponse> findAllByStatus(@Param("status") AuctionStatus status, Pageable pageable);

    // ── Paginated list — all auctions, no status filter ──────────────────────
    @Query("""
        SELECT new com.example.auction.domain.models.AuctionCacheResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.baseImageUrl,
            a.startTime,
            a.endTime,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
    """)
    Page<AuctionCacheResponse> findAllCached(Pageable pageable);

    // ── Paginated list — by seller ────────────────────────────────────────────
    @Query("""
        SELECT new com.example.auction.domain.models.AuctionCacheResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.baseImageUrl,
            a.startTime,
            a.endTime,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        WHERE a.seller.id = :sellerId
    """)
    Page<AuctionCacheResponse> findBySellerId(@Param("sellerId") String sellerId, Pageable pageable);

    // ── Search — by title, with optional status filter ────────────────────────
    @Query("""
        SELECT new com.example.auction.domain.models.AuctionCacheResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.baseImageUrl,
            a.startTime,
            a.endTime,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))
          AND (:status IS NULL OR a.status = :status)
    """)
    Page<AuctionCacheResponse> findByTitleContainingIgnoreCase(@Param("title") String title, @Param("status") AuctionStatus status, Pageable pageable);

    // ── Search — by title, no status filter ──────────────────────────────────
    @Query("""
        SELECT new com.example.auction.domain.models.AuctionCacheResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.baseImageUrl,
            a.startTime,
            a.endTime,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))
    """)
    Page<AuctionCacheResponse> findByTitleContainingIgnoreCaseAll(@Param("title") String title, Pageable pageable);

    // ── Search — by seller + title ────────────────────────────────────────────
    @Query("""
        SELECT new com.example.auction.domain.models.AuctionCacheResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.baseImageUrl,
            a.startTime,
            a.endTime,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        WHERE a.seller.id = :sellerId
          AND LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))
    """)
    Page<AuctionCacheResponse> findBySellerIdAndTitleContainingIgnoreCase(@Param("sellerId") String sellerId, @Param("title") String title, Pageable pageable);

    // ── Scheduler queries — unchanged, return entities ────────────────────────
    @Query("""
        SELECT a FROM AuctionEntity a
        WHERE a.status = 'DRAFT'
          AND a.startTime <= CURRENT_TIMESTAMP
    """)
    List<AuctionEntity> findDraftStartedAuctions();

    @Query("""
        SELECT a FROM AuctionEntity a
        WHERE a.status = com.example.auction.domain.models.AuctionStatus.OPEN
          AND a.endTime <= CURRENT_TIMESTAMP
    """)
    List<AuctionEntity> findOpenAuctionsPastEndTime();

    // ── Mutations ─────────────────────────────────────────────────────────────
    @Modifying
    @Query("""
        UPDATE AuctionEntity a
        SET a.status = 'COMPLETED',
            a.winnerId = :winner,
            a.winningAmount = :price
        WHERE a.uid = :auctionId
    """)
    void markCompleted(@Param("auctionId") UUID auctionId, @Param("winner") String winner, @Param("price") BigDecimal price);
}