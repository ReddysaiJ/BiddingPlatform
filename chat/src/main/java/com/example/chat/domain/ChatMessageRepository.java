package com.example.chat.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    Page<ChatMessageEntity> findByRoomOrderBySentAtAsc(ChatRoomEntity room, Pageable pageable);
}
