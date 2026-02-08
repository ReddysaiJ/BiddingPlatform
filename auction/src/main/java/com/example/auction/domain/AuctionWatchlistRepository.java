package com.example.auction.domain;

import com.example.auction.domain.models.AuctionResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface AuctionWatchlistRepository extends JpaRepository<WatchlistEntity, Long> {

    @Query("""
        select new com.example.auction.domain.models.AuctionResponse(
            a.uid, a.title,
            a.description, a.basePrice,
            a.seller.name, a.status,
            a.startTime, a.endTime,
            a.createdAt, a.updatedAt
        )
        from WatchlistEntity w
        join w.auction a
        where w.userId = :userId
    """)
    Page<AuctionResponse> findWatchedAuctions(Pageable pageable, @Param("userId") String userId);


    boolean existsByUserIdAndAuction_Id(@NotBlank(message = "Customer ID is required") String id, Long id1);

    Optional<WatchlistEntity> findByUserIdAndAuction_Id(@NotBlank(message = "Customer ID is required") String id, Long id1);
}
