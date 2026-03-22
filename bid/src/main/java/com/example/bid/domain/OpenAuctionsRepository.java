package com.example.bid.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OpenAuctionsRepository extends JpaRepository<OpenAuctionsEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM OpenAuctionsEntity a WHERE a.auctionUid = :id")
    Optional<OpenAuctionsEntity> findByIdForUpdate(UUID id);
}
