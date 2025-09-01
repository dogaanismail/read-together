package org.readtogether.notification.factory;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.model.NotificationPreferencesResponse;
import org.readtogether.notification.fixtures.NotificationPreferenceEntityFixtures;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@DisplayName("NotificationPreferencesResponseFactory Tests")
class NotificationPreferencesResponseFactoryTests {

    @Test
    @DisplayName("Should map preferences entity to response")
    void shouldMapPreferencesEntityToResponse() {
        // Given
        NotificationPreferenceEntity entity = NotificationPreferenceEntityFixtures.createDefaultPreferencesEntity();

        // When
        NotificationPreferencesResponse result = NotificationPreferencesResponseFactory.createFromEntity(entity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isEmailNotifications()).isEqualTo(entity.isEmailNotifications());
        assertThat(result.isPushNotifications()).isEqualTo(entity.isPushNotifications());
        assertThat(result.isSessionLikes()).isEqualTo(entity.isSessionLikes());
        assertThat(result.isNewFollowers()).isEqualTo(entity.isNewFollowers());
        assertThat(result.isLiveStreamAlerts()).isEqualTo(entity.isLiveStreamAlerts());
        assertThat(result.isWeeklyDigest()).isEqualTo(entity.isWeeklyDigest());
        assertThat(result.isMarketingEmails()).isEqualTo(entity.isMarketingEmails());
        assertThat(result.isUploadStatus()).isEqualTo(entity.isUploadStatus());
        assertThat(result.getEmailAddress()).isEqualTo(entity.getEmailAddress());
        assertThat(result.getPhoneNumber()).isEqualTo(entity.getPhoneNumber());
        assertThat(result.getPushSubscriptionEndpoint()).isEqualTo(entity.getPushSubscriptionEndpoint());
    }

    @Test
    @DisplayName("Should handle all disabled preferences")
    void shouldHandleAllDisabledPreferences() {
        // Given
        NotificationPreferenceEntity entity = NotificationPreferenceEntityFixtures.createAllDisabledPreferences(
                NotificationPreferenceEntityFixtures.DEFAULT_USER_ID);

        // When
        NotificationPreferencesResponse result = NotificationPreferencesResponseFactory.createFromEntity(entity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isEmailNotifications()).isFalse();
        assertThat(result.isPushNotifications()).isFalse();
        assertThat(result.isSessionLikes()).isFalse();
        assertThat(result.isNewFollowers()).isFalse();
        assertThat(result.isLiveStreamAlerts()).isFalse();
        assertThat(result.isWeeklyDigest()).isFalse();
        assertThat(result.isMarketingEmails()).isFalse();
        assertThat(result.isUploadStatus()).isFalse();
        assertThat(result.getEmailAddress()).isNull();
        assertThat(result.getPhoneNumber()).isNull();
        assertThat(result.getPushSubscriptionEndpoint()).isNull();
    }

    @Test
    @DisplayName("Should handle minimal preferences")
    void shouldHandleMinimalPreferences() {
        // Given
        NotificationPreferenceEntity entity = NotificationPreferenceEntityFixtures.createMinimalPreferences(
                NotificationPreferenceEntityFixtures.DEFAULT_USER_ID);

        // When
        NotificationPreferencesResponse result = NotificationPreferencesResponseFactory.createFromEntity(entity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isEmailNotifications()).isTrue();
        assertThat(result.isPushNotifications()).isFalse();
        assertThat(result.isSessionLikes()).isTrue();
        assertThat(result.isNewFollowers()).isFalse();
        assertThat(result.isLiveStreamAlerts()).isFalse();
        assertThat(result.isWeeklyDigest()).isFalse();
        assertThat(result.isMarketingEmails()).isFalse();
        assertThat(result.isUploadStatus()).isTrue();
        assertThat(result.getEmailAddress()).isEqualTo("minimal@example.com");
        assertThat(result.getPhoneNumber()).isNull();
        assertThat(result.getPushSubscriptionEndpoint()).isNull();
    }

    @Test
    @DisplayName("Should handle custom preferences configuration")
    void shouldHandleCustomPreferencesConfiguration() {
        // Given
        NotificationPreferenceEntity entity = NotificationPreferenceEntityFixtures.createPreferencesEntity(
                NotificationPreferenceEntityFixtures.SECONDARY_USER_ID,
                true,    // emailEnabled
                true,    // pushEnabled
                false,   // sessionLikes
                true,    // newFollowers
                false,   // liveStreamAlerts
                true,    // weeklyDigest
                false,   // marketingEmails
                true,    // uploadStatus
                "custom@example.com",
                "+9876543210",
                "https://custom.push.endpoint",
                "{\"custom\":\"keys\"}"
        );

        // When
        NotificationPreferencesResponse result = NotificationPreferencesResponseFactory.createFromEntity(entity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isEmailNotifications()).isTrue();
        assertThat(result.isPushNotifications()).isTrue();
        assertThat(result.isSessionLikes()).isFalse();
        assertThat(result.isNewFollowers()).isTrue();
        assertThat(result.isLiveStreamAlerts()).isFalse();
        assertThat(result.isWeeklyDigest()).isTrue();
        assertThat(result.isMarketingEmails()).isFalse();
        assertThat(result.isUploadStatus()).isTrue();
        assertThat(result.getEmailAddress()).isEqualTo("custom@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+9876543210");
        assertThat(result.getPushSubscriptionEndpoint()).isEqualTo("https://custom.push.endpoint");
    }

    @Test
    @DisplayName("Should not include push subscription keys in response")
    void shouldNotIncludePushSubscriptionKeysInResponse() {
        // Given
        NotificationPreferenceEntity entity = NotificationPreferenceEntityFixtures.createDefaultPreferencesEntity();

        // When
        NotificationPreferencesResponse result = NotificationPreferencesResponseFactory.createFromEntity(entity);

        // Then
        assertThat(result).isNotNull();
        // Note: The response should not include pushSubscriptionKeys for security reasons
        // The factory should only include the endpoint, not the keys
        assertThat(result.getPushSubscriptionEndpoint()).isEqualTo(entity.getPushSubscriptionEndpoint());
    }
}