package org.readtogether.chat.repository;

import org.readtogether.chat.entity.ChatParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipantEntity, UUID> {

    List<ChatParticipantEntity> findByChatRoomIdAndIsActiveTrue(UUID chatRoomId);

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

}