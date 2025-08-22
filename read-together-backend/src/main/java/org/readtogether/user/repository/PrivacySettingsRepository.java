package org.readtogether.user.repository;

import org.readtogether.user.entity.PrivacySettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrivacySettingsRepository extends JpaRepository<PrivacySettingsEntity, UUID> {

    Optional<PrivacySettingsEntity> findByUserId(UUID userId);

}
