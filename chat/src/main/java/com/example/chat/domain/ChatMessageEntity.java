package com.example.chat.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages",
        indexes = @Index(name = "idx_chat_messages_room", columnList = "room_id, sent_at"))
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_msg_id_gen")
    @SequenceGenerator(name = "chat_msg_id_gen", sequenceName = "chat_msg_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false, updatable = false)
    private ChatRoomEntity room;

    @Column(name = "sender_id", nullable = false, updatable = false)
    private String senderId;

    @Column(name = "sender_name", nullable = false, updatable = false)
    private String senderName;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    protected ChatMessageEntity() {}

    public ChatMessageEntity(ChatRoomEntity room, String senderId, String senderName, String content) {
        this.room       = room;
        this.senderId   = senderId;
        this.senderName = senderName;
        this.content    = content;
    }

    @PrePersist
    void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public ChatRoomEntity getRoom() {
        return room;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
}
