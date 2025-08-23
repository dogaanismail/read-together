package org.readtogether.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.repository.NotificationPreferencesRepository;
import org.readtogether.notification.model.NotificationPreferencesUpdateRequest;
import org.readtogether.notification.fixtures.NotificationPreferenceEntityFixtures;
import org.readtogether.notification.fixtures.NotificationRequestFixtures;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationPreferencesService Tests")
class NotificationPreferencesServiceTests {

    @Mock
    private NotificationPreferencesRepository preferencesRepository;

    @InjectMocks
    private NotificationPreferencesService preferencesService;

    @Test
    @DisplayName("Should get user preferences when they exist")
    void shouldGetUserPreferencesWhenTheyExist() {
        // Given
        UUID userId = NotificationPreferenceEntityFixtures.DEFAULT_USER_ID;
        NotificationPreferenceEntity expectedEntity = NotificationPreferenceEntityFixtures.createDefaultPreferencesEntity(userId);
        
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.of(expectedEntity));

        // When
        NotificationPreferenceEntity result = preferencesService.getUserPreferences(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.isEmailNotifications()).isEqualTo(expectedEntity.isEmailNotifications());
        assertThat(result.isPushNotifications()).isEqualTo(expectedEntity.isPushNotifications());
        
        verify(preferencesRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Should return default preferences when they don't exist (not persisted)")
    void shouldReturnDefaultPreferencesWhenTheyDontExist() {
        // Given
        UUID userId = UUID.randomUUID();
        
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When
        NotificationPreferenceEntity result = preferencesService.getUserPreferences(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        // Default values from factory
        assertThat(result.isEmailNotifications()).isTrue();
        assertThat(result.isPushNotifications()).isTrue();
        
        verify(preferencesRepository).findByUserId(userId);
        // Note: getUserPreferences does NOT save when preferences don't exist - it returns defaults from factory
        verify(preferencesRepository, never()).save(any(NotificationPreferenceEntity.class));
    }

    @Test
    @DisplayName("Should update preferences")
    void shouldUpdatePreferences() {
        // Given
        UUID userId = NotificationPreferenceEntityFixtures.DEFAULT_USER_ID;
        NotificationPreferenceEntity existingEntity = NotificationPreferenceEntityFixtures.createDefaultPreferencesEntity(userId);
        NotificationPreferencesUpdateRequest updateRequest = NotificationRequestFixtures.createDefaultPreferencesUpdateRequest();
        
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.of(existingEntity));
        when(preferencesRepository.save(any(NotificationPreferenceEntity.class))).thenReturn(existingEntity);

        // When
        NotificationPreferenceEntity result = preferencesService.updatePreferences(userId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(preferencesRepository).findByUserId(userId);
        verify(preferencesRepository).save(any(NotificationPreferenceEntity.class));
    }

    @Test
    @DisplayName("Should create new preferences when updating non-existing user")
    void shouldCreateNewPreferencesWhenUpdatingNonExistingUser() {
        // Given
        UUID userId = UUID.randomUUID();
        NotificationPreferencesUpdateRequest updateRequest = NotificationRequestFixtures.createDefaultPreferencesUpdateRequest();
        NotificationPreferenceEntity savedEntity = NotificationPreferenceEntityFixtures.createDefaultPreferencesEntity(userId);
        
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any(NotificationPreferenceEntity.class))).thenReturn(savedEntity);

        // When
        NotificationPreferenceEntity result = preferencesService.updatePreferences(userId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        
        verify(preferencesRepository).findByUserId(userId);
        verify(preferencesRepository).save(any(NotificationPreferenceEntity.class));
        // Note: updatePreferences creates an entity from DTO, doesn't call existsByUserId or createDefaultPreferences
    }

    @Test
    @DisplayName("Should create default preferences")
    void shouldCreateDefaultPreferences() {
        // Given
        UUID userId = UUID.randomUUID();
        NotificationPreferenceEntity savedEntity = NotificationPreferenceEntityFixtures.createDefaultPreferencesEntity(userId);
        
        when(preferencesRepository.existsByUserId(userId)).thenReturn(false);
        when(preferencesRepository.save(any(NotificationPreferenceEntity.class))).thenReturn(savedEntity);

        // When
        NotificationPreferenceEntity result = preferencesService.createDefaultPreferences(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        
        verify(preferencesRepository).existsByUserId(userId);
        verify(preferencesRepository).save(any(NotificationPreferenceEntity.class));
    }

    @Test
    @DisplayName("Should return existing preferences when creating defaults for existing user")
    void shouldReturnExistingPreferencesWhenCreatingDefaultsForExistingUser() {
        // Given
        UUID userId = NotificationPreferenceEntityFixtures.DEFAULT_USER_ID;
        NotificationPreferenceEntity existingEntity = NotificationPreferenceEntityFixtures.createDefaultPreferencesEntity(userId);
        
        when(preferencesRepository.existsByUserId(userId)).thenReturn(true);
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.of(existingEntity));

        // When
        NotificationPreferenceEntity result = preferencesService.createDefaultPreferences(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        
        verify(preferencesRepository).existsByUserId(userId);
        verify(preferencesRepository).findByUserId(userId);
        verify(preferencesRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update push subscription")
    void shouldUpdatePushSubscription() {
        // Given
        UUID userId = NotificationPreferenceEntityFixtures.DEFAULT_USER_ID;
        String endpoint = "https://push.example.com/new-endpoint";
        String keys = "{\"p256dh\":\"newkey\",\"auth\":\"newauth\"}";
        NotificationPreferenceEntity existingEntity = NotificationPreferenceEntityFixtures.createDefaultPreferencesEntity(userId);
        
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.of(existingEntity));
        when(preferencesRepository.save(any(NotificationPreferenceEntity.class))).thenReturn(existingEntity);

        // When
        preferencesService.updatePushSubscription(userId, endpoint, keys);

        // Then
        verify(preferencesRepository).findByUserId(userId);
        verify(preferencesRepository).save(argThat(entity -> 
                entity.getPushSubscriptionEndpoint().equals(endpoint) &&
                entity.getPushSubscriptionKeys().equals(keys) &&
                entity.getUpdatedAt() != null
        ));
    }

    @Test
    @DisplayName("Should create preferences and update push subscription when user doesn't exist")
    void shouldCreatePreferencesAndUpdatePushSubscriptionWhenUserDoesntExist() {
        // Given
        UUID userId = UUID.randomUUID();
        String endpoint = "https://push.example.com/endpoint";
        String keys = "{\"p256dh\":\"key\",\"auth\":\"auth\"}";
        NotificationPreferenceEntity createdEntity = NotificationPreferenceEntityFixtures.createDefaultPreferencesEntity(userId);
        
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(preferencesRepository.existsByUserId(userId)).thenReturn(false);
        when(preferencesRepository.save(any(NotificationPreferenceEntity.class))).thenReturn(createdEntity);

        // When
        preferencesService.updatePushSubscription(userId, endpoint, keys);

        // Then
        verify(preferencesRepository).findByUserId(userId);
        verify(preferencesRepository, times(2)).save(any(NotificationPreferenceEntity.class)); // Once for creation, once for update
    }

    @Test
    @DisplayName("Should handle null endpoint and keys in push subscription update")
    void shouldHandleNullEndpointAndKeysInPushSubscriptionUpdate() {
        // Given
        UUID userId = NotificationPreferenceEntityFixtures.DEFAULT_USER_ID;
        NotificationPreferenceEntity existingEntity = NotificationPreferenceEntityFixtures.createDefaultPreferencesEntity(userId);
        
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.of(existingEntity));
        when(preferencesRepository.save(any(NotificationPreferenceEntity.class))).thenReturn(existingEntity);

        // When
        preferencesService.updatePushSubscription(userId, null, null);

        // Then
        verify(preferencesRepository).findByUserId(userId);
        verify(preferencesRepository).save(argThat(entity -> 
                entity.getPushSubscriptionEndpoint() == null &&
                entity.getPushSubscriptionKeys() == null
        ));
    }

    @Test
    @DisplayName("Should handle different preference combinations in update")
    void shouldHandleDifferentPreferenceCombinationsInUpdate() {
        // Given
        UUID userId = NotificationPreferenceEntityFixtures.DEFAULT_USER_ID;
        NotificationPreferenceEntity existingEntity = NotificationPreferenceEntityFixtures.createDefaultPreferencesEntity(userId);
        NotificationPreferencesUpdateRequest customRequest = NotificationRequestFixtures.createPreferencesUpdateRequest(
                false, true, true, false, false, true, false, true
        );
        
        when(preferencesRepository.findByUserId(userId)).thenReturn(Optional.of(existingEntity));
        when(preferencesRepository.save(any(NotificationPreferenceEntity.class))).thenReturn(existingEntity);

        // When
        NotificationPreferenceEntity result = preferencesService.updatePreferences(userId, customRequest);

        // Then
        assertThat(result).isNotNull();
        verify(preferencesRepository).findByUserId(userId);
        verify(preferencesRepository).save(any(NotificationPreferenceEntity.class));
    }
}