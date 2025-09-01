package org.readtogether.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.user.common.enums.ProfileVisibility;
import org.readtogether.user.entity.PrivacySettingsEntity;
import org.readtogether.user.factory.PrivacySettingsFactory;
import org.readtogether.user.model.request.PrivacySettingsUpdateRequest;
import org.readtogether.user.repository.PrivacySettingsRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PrivacySettingsService Tests")
class PrivacySettingsServiceTests {

    @Mock
    private PrivacySettingsRepository privacySettingsRepository;

    @InjectMocks
    private PrivacySettingsService privacySettingsService;

    @Test
    @DisplayName("Should return existing privacy settings when found")
    void shouldReturnExistingPrivacySettingsWhenFound() {
        UUID userId = UUID.randomUUID();
        PrivacySettingsEntity existing = PrivacySettingsEntity.builder()
                .userId(userId)
                .profileVisibility(ProfileVisibility.FOLLOWERS)
                .showEmail(true)
                .showOnlineStatus(false)
                .allowMessages(true)
                .showReadingSessions(false)
                .searchable(true)
                .build();

        when(privacySettingsRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        PrivacySettingsEntity result = privacySettingsService.getUserPrivacySettings(userId);

        assertThat(result).isSameAs(existing);
        verify(privacySettingsRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Should update privacy settings when existing record found")
    void shouldUpdatePrivacySettingsWhenExistingFound() {
        UUID userId = UUID.randomUUID();
        PrivacySettingsUpdateRequest request = PrivacySettingsUpdateRequest.builder()
                .profileVisibility(ProfileVisibility.PRIVATE)
                .showEmail(true)
                .showOnlineStatus(false)
                .allowMessages(false)
                .showReadingSessions(false)
                .searchable(false)
                .build();

        PrivacySettingsEntity existing = PrivacySettingsEntity.builder()
                .userId(userId)
                .profileVisibility(ProfileVisibility.PUBLIC)
                .showEmail(false)
                .showOnlineStatus(true)
                .allowMessages(true)
                .showReadingSessions(true)
                .searchable(true)
                .build();

        when(privacySettingsRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(privacySettingsRepository.save(any(PrivacySettingsEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        PrivacySettingsEntity result = privacySettingsService.updatePrivacySettings(userId, request);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getUpdatedAt()).isNotNull();

        verify(privacySettingsRepository).findByUserId(userId);
        verify(privacySettingsRepository).save(existing);
    }

    @Test
    @DisplayName("Should create and save privacy settings when none exists")
    void shouldCreateAndSavePrivacySettingsWhenNoneExists() {
        UUID userId = UUID.randomUUID();

        PrivacySettingsUpdateRequest request = PrivacySettingsUpdateRequest.builder()
                .profileVisibility(ProfileVisibility.FOLLOWERS)
                .showEmail(true)
                .showOnlineStatus(true)
                .allowMessages(true)
                .showReadingSessions(true)
                .searchable(true)
                .build();

        when(privacySettingsRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(privacySettingsRepository.save(any(PrivacySettingsEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PrivacySettingsEntity result = privacySettingsService.updatePrivacySettings(userId, request);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getProfileVisibility()).isEqualTo(request.getProfileVisibility());
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should allow access when owner regardless of settings")
    void shouldAllowAccessWhenOwnerRegardlessOfSettings() {
        UUID userId = UUID.randomUUID();
        PrivacySettingsEntity settings = PrivacySettingsEntity.builder()
                .userId(userId)
                .profileVisibility(ProfileVisibility.PRIVATE)
                .allowMessages(false)
                .build();

        when(privacySettingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));

        boolean canAccess = privacySettingsService.canAccessProfile(userId, userId, false);
        assertThat(canAccess).isTrue();
    }

    @Test
    @DisplayName("Should allow access when profile is PUBLIC")
    void shouldAllowAccessWhenProfileIsPublic() {
        UUID target = UUID.randomUUID();
        UUID current = UUID.randomUUID();
        PrivacySettingsEntity settings = PrivacySettingsEntity.builder()
                .userId(target)
                .profileVisibility(ProfileVisibility.PUBLIC)
                .build();
        when(privacySettingsRepository.findByUserId(target)).thenReturn(Optional.of(settings));

        boolean canAccess = privacySettingsService.canAccessProfile(target, current, false);
        assertThat(canAccess).isTrue();
    }

    @Test
    @DisplayName("Should allow access for followers when profile is FOLLOWERS and following is true")
    void shouldAllowAccessForFollowersWhenFollowingTrue() {
        UUID target = UUID.randomUUID();
        UUID current = UUID.randomUUID();
        PrivacySettingsEntity settings = PrivacySettingsEntity.builder()
                .userId(target)
                .profileVisibility(ProfileVisibility.FOLLOWERS)
                .build();
        when(privacySettingsRepository.findByUserId(target)).thenReturn(Optional.of(settings));

        boolean canAccess = privacySettingsService.canAccessProfile(target, current, true);
        assertThat(canAccess).isTrue();

        boolean cannotAccess = privacySettingsService.canAccessProfile(target, current, false);
        assertThat(cannotAccess).isFalse();
    }

    @Test
    @DisplayName("Should deny access when profile is PRIVATE and requester is not owner")
    void shouldDenyAccessWhenPrivateAndNotOwner() {
        UUID target = UUID.randomUUID();
        UUID current = UUID.randomUUID();
        PrivacySettingsEntity settings = PrivacySettingsEntity.builder()
                .userId(target)
                .profileVisibility(ProfileVisibility.PRIVATE)
                .build();
        when(privacySettingsRepository.findByUserId(target)).thenReturn(Optional.of(settings));

        boolean canAccess = privacySettingsService.canAccessProfile(target, current, true);
        assertThat(canAccess).isFalse();
    }

    @Test
    @DisplayName("Should not allow sending messages when disabled even if accessible")
    void shouldNotAllowSendingMessagesWhenDisabled() {
        UUID target = UUID.randomUUID();
        UUID current = UUID.randomUUID();
        PrivacySettingsEntity settings = PrivacySettingsEntity.builder()
                .userId(target)
                .profileVisibility(ProfileVisibility.PUBLIC)
                .allowMessages(false)
                .build();
        when(privacySettingsRepository.findByUserId(target)).thenReturn(Optional.of(settings));

        boolean canSend = privacySettingsService.canSendMessage(target, current, true);
        assertThat(canSend).isFalse();
    }

    @Test
    @DisplayName("Should allow sending messages when enabled and profile accessible")
    void shouldAllowSendingMessagesWhenEnabledAndAccessible() {
        UUID target = UUID.randomUUID();
        UUID current = UUID.randomUUID();
        PrivacySettingsEntity settings = PrivacySettingsEntity.builder()
                .userId(target)
                .profileVisibility(ProfileVisibility.FOLLOWERS)
                .allowMessages(true)
                .build();
        when(privacySettingsRepository.findByUserId(target)).thenReturn(Optional.of(settings));

        boolean canSend = privacySettingsService.canSendMessage(target, current, true);
        assertThat(canSend).isTrue();

        boolean cannotSend = privacySettingsService.canSendMessage(target, current, false);
        assertThat(cannotSend).isFalse();
    }

    @Test
    @DisplayName("Should return showEmail flag from existing settings")
    void shouldReturnShowEmailFromExistingSettings() {
        UUID userId = UUID.randomUUID();
        PrivacySettingsEntity settings = PrivacySettingsEntity.builder()
                .userId(userId)
                .showEmail(true)
                .build();
        when(privacySettingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));

        assertThat(privacySettingsService.shouldShowEmail(userId)).isTrue();
    }

    @Test
    @DisplayName("Should return showOnlineStatus from existing settings")
    void shouldReturnShowOnlineStatusFromExistingSettings() {
        UUID userId = UUID.randomUUID();
        PrivacySettingsEntity settings = PrivacySettingsEntity.builder()
                .userId(userId)
                .showOnlineStatus(false)
                .build();
        when(privacySettingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));

        assertThat(privacySettingsService.shouldShowOnlineStatus(userId)).isFalse();
    }

    @Test
    @DisplayName("Should fallback to default showOnlineStatus when settings not found (true)")
    void shouldFallbackToDefaultShowOnlineStatusWhenSettingsMissing() {
        UUID userId = UUID.randomUUID();
        when(privacySettingsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        try (MockedStatic<PrivacySettingsFactory> factoryMock = mockStatic(PrivacySettingsFactory.class)) {
            PrivacySettingsEntity defaults = PrivacySettingsEntity.builder().userId(userId).build();
            factoryMock.when(() -> PrivacySettingsFactory.createDefaultSettings(userId)).thenReturn(defaults);

            assertThat(privacySettingsService.shouldShowOnlineStatus(userId)).isTrue();
        }
    }

    @Test
    @DisplayName("Should return showReadingSessions from existing settings")
    void shouldReturnShowReadingSessionsFromExistingSettings() {
        UUID userId = UUID.randomUUID();
        PrivacySettingsEntity settings = PrivacySettingsEntity.builder()
                .userId(userId)
                .showReadingSessions(false)
                .build();
        when(privacySettingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));

        assertThat(privacySettingsService.shouldShowReadingSessions(userId)).isFalse();
    }

    @Test
    @DisplayName("Should fallback to default showReadingSessions when settings not found (true)")
    void shouldFallbackToDefaultShowReadingSessionsWhenSettingsMissing() {
        UUID userId = UUID.randomUUID();
        when(privacySettingsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        try (MockedStatic<PrivacySettingsFactory> factoryMock = mockStatic(PrivacySettingsFactory.class)) {
            PrivacySettingsEntity defaults = PrivacySettingsEntity.builder().userId(userId).build();
            factoryMock.when(() -> PrivacySettingsFactory.createDefaultSettings(userId)).thenReturn(defaults);

            assertThat(privacySettingsService.shouldShowReadingSessions(userId)).isTrue();
        }
    }

    @Test
    @DisplayName("Should return searchable flag from existing settings")
    void shouldReturnSearchableFromExistingSettings() {
        UUID userId = UUID.randomUUID();
        PrivacySettingsEntity settings = PrivacySettingsEntity.builder()
                .userId(userId)
                .searchable(false)
                .build();
        when(privacySettingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));

        assertThat(privacySettingsService.isSearchable(userId)).isFalse();
    }

    @Test
    @DisplayName("Should fallback to default searchable when settings not found (true)")
    void shouldFallbackToDefaultSearchableWhenSettingsMissing() {
        UUID userId = UUID.randomUUID();
        when(privacySettingsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        try (MockedStatic<PrivacySettingsFactory> factoryMock = mockStatic(PrivacySettingsFactory.class)) {
            PrivacySettingsEntity defaults = PrivacySettingsEntity.builder().userId(userId).build();
            factoryMock.when(() -> PrivacySettingsFactory.createDefaultSettings(userId)).thenReturn(defaults);

            assertThat(privacySettingsService.isSearchable(userId)).isTrue();
        }
    }
}
