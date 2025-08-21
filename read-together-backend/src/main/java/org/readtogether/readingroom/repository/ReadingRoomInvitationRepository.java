package org.readtogether.readingroom.repository;

import org.readtogether.readingroom.entity.ReadingRoomInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadingRoomInvitationRepository extends JpaRepository<ReadingRoomInvitationEntity, UUID> {

    Optional<ReadingRoomInvitationEntity> findByInvitationToken(String invitationToken);

    @Query("SELECT i FROM ReadingRoomInvitationEntity i WHERE i.readingRoom.id = :roomId")
    List<ReadingRoomInvitationEntity> findByReadingRoomId(@Param("roomId") UUID roomId);

    @Query("SELECT i FROM ReadingRoomInvitationEntity i WHERE i.invitedUser.id = :userId AND i.status = 'PENDING'")
    List<ReadingRoomInvitationEntity> findPendingInvitationsByUserId(@Param("userId") UUID userId);

    @Query("SELECT i FROM ReadingRoomInvitationEntity i WHERE i.invitedEmail = :email AND i.status = 'PENDING'")
    List<ReadingRoomInvitationEntity> findPendingInvitationsByEmail(@Param("email") String email);
}
