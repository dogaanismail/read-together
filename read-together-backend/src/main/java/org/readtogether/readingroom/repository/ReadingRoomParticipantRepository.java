package org.readtogether.readingroom.repository;

import org.readtogether.readingroom.entity.ReadingRoomParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadingRoomParticipantRepository extends JpaRepository<ReadingRoomParticipantEntity, UUID> {

    Optional<ReadingRoomParticipantEntity> findByReadingRoomIdAndUserId(
            UUID roomId,
            UUID userId);

    @Query("SELECT COUNT(p) FROM ReadingRoomParticipantEntity p WHERE p.readingRoom.id = :roomId AND p.status = 'JOINED'")
    Integer countActiveParticipantsByRoomId(@Param("roomId") UUID roomId);
}
