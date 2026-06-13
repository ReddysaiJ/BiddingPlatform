package com.example.bid.domain;

import com.example.bid.domain.models.BidResponse;
import com.example.bid.domain.models.BidResponseAuction;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRepository extends JpaRepository<BidEntity, Long> {

    @Query("""
        SELECT new com.example.bid.domain.models.BidResponse(
            b.id,
            b.auctionId,
            b.auctionTitle,
            b.userId,
            b.amount,
            b.createdAt
        )
        FROM BidEntity b
        WHERE b.auctionId = :auctionId
    """)
    Page<BidResponse> findByAuctionIdOrderByCreatedAtDesc(Pageable pageable, UUID auctionId);

    @Query("""
        SELECT new com.example.bid.domain.models.BidResponseAuction(
            b.auctionId,
            b.auctionTitle,
            count(b)
        )
        FROM BidEntity b
        WHERE b.userId = :userId
        GROUP BY b.auctionId, b.auctionTitle
        ORDER BY MAX(b.createdAt) DESC
    """)
    Page<BidResponseAuction> findAuctionByUserId(Pageable pageable, String userId);

    @Query("""
        SELECT new com.example.bid.domain.models.BidResponse(
            b.id,
            b.auctionId,
            b.auctionTitle,
            b.userId,
            b.amount,
            b.createdAt
        )
        FROM BidEntity b
        WHERE b.userId = :userId
            AND
              b.auctionId = :auctionId
    """)
    Page<BidResponse> findByUserIdAndAuctionIdOrderByCreatedAtDesc(Pageable pageable, String userId, UUID auctionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b
            FROM BidEntity b
            WHERE b.auctionId = :auctionId
            ORDER BY b.amount DESC
            """)
    Page<BidEntity> findTopByAuctionIdOrderByAmountDesc(Pageable page, UUID auctionId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("""
        SELECT b
        FROM BidEntity b
        WHERE b.auctionId = :auctionId
        ORDER BY b.amount DESC
    """)
    Page<BidEntity> findHighestBid(Pageable pageable, UUID auctionId);

    List<BidEntity> findAllByAuctionId(UUID auctionId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE BidEntity b
        SET b.winner = CASE WHEN b.userId = :winnerUserId THEN true ELSE false END,
            b.finalPrice = :finalPrice
        WHERE b.auctionId = :auctionId
    """)
    void markAuctionClosed(UUID auctionId, String winnerUserId, BigDecimal finalPrice);

    @Modifying
    @Query("""
        update BidEntity b
        set b.winner = true
        where b.id = :bidId
    """)
    void markWinner(Long bidId);

    @Query("""
        select b from BidEntity b
        where b.auctionId = :auctionId
        order by b.amount desc
        limit 1
    """)
    Optional<BidEntity> findHighestBid(UUID auctionId);
}
