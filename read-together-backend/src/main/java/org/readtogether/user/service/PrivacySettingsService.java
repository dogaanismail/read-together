package org.readtogether.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.user.entity.PrivacySettingsEntity;
import org.readtogether.user.factory.PrivacySettingsFactory;
import org.readtogether.user.model.request.PrivacySettingsUpdateRequest;
import org.readtogether.user.repository.PrivacySettingsRepository;
import org.readtogether.user.utils.PrivacySettingsUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivacySettingsService {

    private final PrivacySettingsRepository privacySettingsRepository;

    @Transactional(readOnly = true)
    public PrivacySettingsEntity getUserPrivacySettings(
            UUID userId) {

        return privacySettingsRepository.findByUserId(userId)
                .orElse(PrivacySettingsFactory.createDefaultSettings(userId));
    }

    @Transactional
    public PrivacySettingsEntity updatePrivacySettings(
            UUID userId,
            PrivacySettingsUpdateRequest request) {

        PrivacySettingsEntity existing = privacySettingsRepository.findByUserId(userId)
                .orElse(PrivacySettingsFactory.createFromDto(userId, request));

        PrivacySettingsEntity updates = PrivacySettingsFactory.createFromDto(userId, request);
        PrivacySettingsUtils.applyUpdates(existing, updates);
        existing.setUpdatedAt(Instant.now());

        return privacySettingsRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public boolean canAccessProfile(
            UUID targetUserId,
            UUID currentUserId,
            boolean isFollowing) {

        PrivacySettingsEntity settings = privacySettingsRepository.findByUserId(targetUserId)
                .orElse(PrivacySettingsFactory.createDefaultSettings(targetUserId));
        boolean isOwner = targetUserId.equals(currentUserId);

        return PrivacySettingsUtils.isProfileAccessible(settings, isFollowing, isOwner);
    }

    @Transactional(readOnly = true)
    public boolean canSendMessage(
            UUID targetUserId,
            UUID currentUserId,
            boolean isFollowing) {

        PrivacySettingsEntity settings = privacySettingsRepository.findByUserId(targetUserId)
                .orElse(PrivacySettingsFactory.createDefaultSettings(targetUserId));
        boolean isOwner = targetUserId.equals(currentUserId);

        return PrivacySettingsUtils.canSendMessage(settings, isFollowing, isOwner);
    }

    @Transactional(readOnly = true)
    public boolean shouldShowEmail(
            UUID userId) {

        PrivacySettingsEntity settings = privacySettingsRepository.findByUserId(userId)
                .orElse(PrivacySettingsFactory.createDefaultSettings(userId));
        return settings.isShowEmail();
    }

    @Transactional(readOnly = true)
    public boolean shouldShowOnlineStatus(
            UUID userId) {

        PrivacySettingsEntity settings = privacySettingsRepository.findByUserId(userId)
                .orElse(PrivacySettingsFactory.createDefaultSettings(userId));
        return settings.isShowOnlineStatus();
    }

    @Transactional(readOnly = true)
    public boolean shouldShowReadingSessions(
            UUID userId) {

        PrivacySettingsEntity settings = privacySettingsRepository.findByUserId(userId)
                .orElse(PrivacySettingsFactory.createDefaultSettings(userId));
        return settings.isShowReadingSessions();
    }

    @Transactional(readOnly = true)
    public boolean isSearchable(
            UUID userId) {

        PrivacySettingsEntity settings = privacySettingsRepository.findByUserId(userId)
                .orElse(PrivacySettingsFactory.createDefaultSettings(userId));
        return settings.isSearchable();
    }
}
