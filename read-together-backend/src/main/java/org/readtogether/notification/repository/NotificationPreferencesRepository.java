package org.readtogether.notification.repository;

import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferenceEntity, UUID> {

    Optional<NotificationPreferenceEntity> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
