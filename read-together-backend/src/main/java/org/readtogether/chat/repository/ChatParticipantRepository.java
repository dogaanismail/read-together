package org.readtogether.chat.repository;

import org.readtogether.chat.entity.ChatParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipantEntity, UUID> {

    List<ChatParticipantEntity> findByChatRoomIdAndIsActiveTrue(UUID chatRoomId);

    Optional<ChatParticipantEntity> findByChatRoomIdAndUserIdAndIsActiveTrue(
        UUID chatRoomId,
        UUID userId
    );

    @Query("""
        SELECT cp FROM chat_participants cp
        WHERE cp.userId = :userId
        AND cp.isActive = true
        AND cp.chatRoomId IN (
            SELECT cr.id FROM chat_rooms cr
            WHERE cr.isActive = true
        )
        ORDER BY cp.lastReadAt DESC
        """)
    List<ChatParticipantEntity> findUserActiveParticipations(@Param("userId") UUID userId);

    @Modifying
    @Query("""
        UPDATE chat_participants
        SET unreadCount = 0, lastReadAt = :readAt
        WHERE chatRoomId = :chatRoomId AND userId = :userId
        """)
    void markAsRead(
        @Param("chatRoomId") UUID chatRoomId,
        @Param("userId") UUID userId,
        @Param("readAt") Instant readAt
    );

    @Modifying
    @Query("""
        UPDATE chat_participants
        SET unreadCount = unreadCount + 1
        WHERE chatRoomId = :chatRoomId AND userId != :senderId AND isActive = true
        """)
    void incrementUnreadCount(
        @Param("chatRoomId") UUID chatRoomId,
        @Param("senderId") UUID senderId
    );

    long countByChatRoomIdAndIsActiveTrue(UUID chatRoomId);
}