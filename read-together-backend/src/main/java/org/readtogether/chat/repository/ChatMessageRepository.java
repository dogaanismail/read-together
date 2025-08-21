package org.readtogether.chat.repository;

import org.readtogether.chat.entity.ChatMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID> {

    @Query("""
        SELECT cm FROM chat_messages cm
        WHERE cm.chatRoomId = :chatRoomId
        AND cm.isDeleted = false
        ORDER BY cm.sentAt DESC
        """)
    Page<ChatMessageEntity> findByChatRoomIdOrderBySentAtDesc(
        @Param("chatRoomId") UUID chatRoomId,
        Pageable pageable
    );

    @Query("""
        SELECT cm FROM chat_messages cm
        WHERE cm.chatRoomId = :chatRoomId
        AND cm.sentAt < :beforeTimestamp
        AND cm.isDeleted = false
        ORDER BY cm.sentAt DESC
        """)
    Page<ChatMessageEntity> findByChatRoomIdBeforeTimestamp(
        @Param("chatRoomId") UUID chatRoomId,
        @Param("beforeTimestamp") Instant beforeTimestamp,
        Pageable pageable
    );

    @Query("""
        SELECT cm FROM chat_messages cm
        WHERE cm.chatRoomId = :chatRoomId
        AND cm.isDeleted = false
        ORDER BY cm.sentAt DESC
        LIMIT 1
        """)
    Optional<ChatMessageEntity> findLastMessageByChatRoomId(@Param("chatRoomId") UUID chatRoomId);

    long countByChatRoomIdAndSentAtAfterAndIsDeletedFalse(
        UUID chatRoomId,
        Instant afterTimestamp
    );

    @Query("""
        SELECT COUNT(cm) FROM chat_messages cm
        WHERE cm.chatRoomId = :chatRoomId
        AND cm.senderId != :userId
        AND cm.sentAt > :lastReadAt
        AND cm.isDeleted = false
        """)
    long countUnreadMessages(
        @Param("chatRoomId") UUID chatRoomId,
        @Param("userId") UUID userId,
        @Param("lastReadAt") Instant lastReadAt
    );
}