package org.readtogether.user.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.readtogether.user.entity.PrivacySettingsEntity;
import org.readtogether.user.common.enums.ProfileVisibility;
import org.readtogether.user.fixtures.PrivacySettingsFixtures;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.readtogether.user.fixtures.UserEntityFixtures.DEFAULT_USER_ID;
import static org.readtogether.user.fixtures.UserEntityFixtures.SECONDARY_USER_ID;

@DisplayName("PrivacySettingsFactory Tests")
class PrivacySettingsFactoryTests {

    @Test
    @DisplayName("Should create default privacy settings with correct default values")
    void shouldCreateDefaultSettings() {
        // When
        PrivacySettingsEntity result = PrivacySettingsFactory.createDefaultSettings(DEFAULT_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(result.getProfileVisibility()).isEqualTo(ProfileVisibility.PUBLIC);
        assertThat(result.isShowEmail()).isFalse();
        assertThat(result.isShowOnlineStatus()).isTrue();
        assertThat(result.isAllowMessages()).isTrue();
        assertThat(result.isShowReadingSessions()).isTrue();
        assertThat(result.isSearchable()).isTrue();
    }

    @Test
    @DisplayName("Should create privacy settings from update request with all values copied")
    void shouldCreateFromUpdateRequest() {
        // Given
        PrivacySettingsEntity updateRequest = PrivacySettingsFixtures.createCustomPrivacySettingsEntity();

        // When
        PrivacySettingsEntity result = PrivacySettingsFactory.createFromUpdateRequest(DEFAULT_USER_ID, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(result.getProfileVisibility()).isEqualTo(updateRequest.getProfileVisibility());
        assertThat(result.isShowEmail()).isEqualTo(updateRequest.isShowEmail());
        assertThat(result.isShowOnlineStatus()).isEqualTo(updateRequest.isShowOnlineStatus());
        assertThat(result.isAllowMessages()).isEqualTo(updateRequest.isAllowMessages());
        assertThat(result.isShowReadingSessions()).isEqualTo(updateRequest.isShowReadingSessions());
        assertThat(result.isSearchable()).isEqualTo(updateRequest.isSearchable());
    }

    @Test
    @DisplayName("Should handle different user IDs correctly")
    void shouldHandleDifferentUserIds() {
        // Given
        UUID differentUserId = SECONDARY_USER_ID;

        // When
        PrivacySettingsEntity result = PrivacySettingsFactory.createDefaultSettings(differentUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(differentUserId);
        assertThat(result.getUserId()).isNotEqualTo(DEFAULT_USER_ID);
    }

}
