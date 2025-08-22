package org.readtogether.user.repository;

import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadingPreferencesRepository extends JpaRepository<ReadingPreferencesEntity, UUID> {

    Optional<ReadingPreferencesEntity> findByUserId(UUID userId);

}
