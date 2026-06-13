package com.example.auction.domain;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.auction.domain.models.AuctionDTO;
import com.example.auction.domain.models.AuctionResponse;
import com.example.auction.domain.models.AuctionStatus;
import jakarta.validation.constraints.NotBlank;
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
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.startTime,
            a.endTime,
    
            case
                when w.id is not null
                then true
                else false
            end,
    
            a.createdAt,
            a.updatedAt
        )
    
        from AuctionEntity a
    
        left join WatchlistEntity w
            on w.auction.uid = a.uid
            and w.userId = :userId
    
        where a.uid = :uid
    """)
    Optional<AuctionResponse> findResponseByUid(@Param("uid") UUID uid, @Param("userId") String userId);

    Optional<AuctionEntity> findByUid(UUID uid);

        @Query("""
        SELECT new com.example.auction.domain.models.AuctionResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.startTime,
            a.endTime,
            CASE WHEN w.id IS NOT NULL THEN true ELSE false END,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        LEFT JOIN WatchlistEntity w
            ON w.auction.uid = a.uid
           AND w.userId = :userId
        WHERE a.seller.id = :sellerId
    """)
    Page<AuctionResponse> findBySellerId(Pageable pageable, @Param("sellerId") String sellerId, @Param("userId") String userId);

//    @Query("""
//            select new com.example.auction.domain.models.AuctionDTO(
//            a.uid,
//            a.status,
//            a.seller.id,
//            a.basePrice
//        )
//        from AuctionEntity a
//        where a.uid = :uid
//        """)
//    Optional<AuctionDTO> findDTOByUid(UUID uid);

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

    @Query("""
        SELECT new com.example.auction.domain.models.AuctionResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.startTime,
            a.endTime,
            CASE
                WHEN EXISTS (
                    SELECT 1
                    FROM WatchlistEntity w
                    WHERE w.auction.uid = a.uid
                      AND w.userId = :userId
                )
                THEN true
                ELSE false
            END,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))
          AND (:status IS NULL OR a.status = :status)
    """)
    Page<AuctionResponse> findByTitleContainingIgnoreCaseForCustomer(String title, String userId, AuctionStatus status, Pageable pageable);

    @Query("""
        SELECT new com.example.auction.domain.models.AuctionResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.startTime,
            a.endTime,
            CASE WHEN w.id IS NOT NULL THEN true ELSE false END,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        LEFT JOIN WatchlistEntity w
            ON w.auction.uid = a.uid
           AND w.userId = :userId
        WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))
    """)
    Page<AuctionResponse> findByTitleContainingIgnoreCase(String title, String userId, Pageable pageable);

    @Query("""
        SELECT new com.example.auction.domain.models.AuctionResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.startTime,
            a.endTime,
            CASE
                WHEN EXISTS (
                    SELECT w
                    FROM WatchlistEntity w
                    WHERE w.auction.uid = a.uid
                      AND w.userId = :userId
                )
                THEN true
                ELSE false
            END,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        WHERE a.seller.id = :sellerId
          AND LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))
    """)
    Page<AuctionResponse> findBySellerIdAndTitleContainingIgnoreCase(String userId, String title, Pageable pageable, String customerId);

    @Query("""
        SELECT new com.example.auction.domain.models.AuctionResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.startTime,
            a.endTime,
            CASE WHEN w.id IS NOT NULL THEN true ELSE false END,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        LEFT JOIN WatchlistEntity w
            ON w.auction = a
           AND w.userId = :userId
        WHERE (:status IS NULL OR a.status = :status)
    """)
    Page<AuctionResponse> findAllForCustomer(@Param("userId") String userId, @Param("status") AuctionStatus status, Pageable pageable);

    @Query("""
        SELECT new com.example.auction.domain.models.AuctionResponse(
            a.uid,
            a.title,
            a.description,
            a.basePrice,
            a.seller.name,
            a.status,
            a.startTime,
            a.endTime,
            CASE WHEN w.id IS NOT NULL THEN true ELSE false END,
            a.createdAt,
            a.updatedAt
        )
        FROM AuctionEntity a
        LEFT JOIN WatchlistEntity w
            ON w.auction = a
           AND w.userId = :userId
    """)
    Page<AuctionResponse> findAllWithWatchStatus(@Param("userId") String userId, Pageable pageable);
}
