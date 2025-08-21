package org.readtogether.readingroom.repository;

import org.readtogether.readingroom.entity.ReadingRoomSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadingRoomSettingsRepository extends JpaRepository<ReadingRoomSettingsEntity, UUID> {

    Optional<ReadingRoomSettingsEntity> findByReadingRoomId(UUID readingRoomId);
}
