package com.example.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    boolean existsByAuctioId(UUID auctionId);

    Optional<ChatRoomEntity> findByAuctionId(UUID auctionId);
}
