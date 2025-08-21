package org.readtogether.readingroom.repository;

import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadingRoomRepository extends JpaRepository<ReadingRoomEntity, UUID> {

    Optional<ReadingRoomEntity> findByRoomCode(String roomCode);

    @Query("SELECT r FROM ReadingRoomEntity r WHERE r.host.id = :hostId ORDER BY r.createdAt DESC")
    List<ReadingRoomEntity> findByHostId(@Param("hostId") UUID hostId);

    @Query("SELECT r FROM ReadingRoomEntity r WHERE r.isPublic = true AND r.status = 'WAITING' ORDER BY r.createdAt DESC")
    List<ReadingRoomEntity> findPublicWaitingRooms();

}
