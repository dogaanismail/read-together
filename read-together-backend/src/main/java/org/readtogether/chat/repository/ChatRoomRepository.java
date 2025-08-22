package org.readtogether.chat.repository;

import org.readtogether.chat.entity.ChatRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, UUID> {

    @Query("""
        SELECT cr FROM chat_rooms cr
        WHERE cr.id IN (
            SELECT cp.chatRoomId FROM chat_participants cp
            WHERE cp.userId = :userId AND cp.isActive = true
        )
        AND cr.isActive = true
        ORDER BY cr.updatedAt DESC
        """)
    Page<ChatRoomEntity> findUserChatRooms(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
        SELECT cr FROM chat_rooms cr
        WHERE cr.type = 'DIRECT'
        AND cr.id IN (
            SELECT cp1.chatRoomId FROM chat_participants cp1
            WHERE cp1.userId = :userId1 AND cp1.isActive = true
        )
        AND cr.id IN (
            SELECT cp2.chatRoomId FROM chat_participants cp2
            WHERE cp2.userId = :userId2 AND cp2.isActive = true
        )
        AND cr.isActive = true
        """)
    Optional<ChatRoomEntity> findDirectChatRoom(
        @Param("userId1") UUID userId1,
        @Param("userId2") UUID userId2
    );

    @Query("""
        SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END
        FROM chat_participants cp
        WHERE cp.chatRoomId = :chatRoomId
        AND cp.userId = :userId
        AND cp.isActive = true
        """)
    boolean isUserParticipant(@Param("chatRoomId") UUID chatRoomId, @Param("userId") UUID userId);

}