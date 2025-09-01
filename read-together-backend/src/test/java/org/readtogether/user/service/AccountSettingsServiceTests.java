package org.readtogether.user.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.notification.entity.NotificationPreferenceEntity;
import org.readtogether.notification.factory.NotificationPreferencesResponseFactory;
import org.readtogether.notification.model.NotificationPreferencesResponse;
import org.readtogether.notification.model.NotificationPreferencesUpdateRequest;
import org.readtogether.notification.service.NotificationPreferencesService;
import org.readtogether.user.entity.PrivacySettingsEntity;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.factory.PrivacySettingsResponseFactory;
import org.readtogether.user.factory.ReadingPreferencesResponseFactory;
import org.readtogether.user.model.request.AccountSettingsUpdateRequest;
import org.readtogether.user.model.request.PrivacySettingsUpdateRequest;
import org.readtogether.user.model.request.ReadingPreferencesUpdateRequest;
import org.readtogether.user.model.response.AccountSettingsResponse;
import org.readtogether.user.model.response.PrivacySettingsResponse;
import org.readtogether.user.model.response.ReadingPreferencesResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountSettingsService Tests")
class AccountSettingsServiceTests {

    @Mock
    private PrivacySettingsService privacySettingsService;

    @Mock
    private ReadingPreferencesService readingPreferencesService;

    @Mock
    private NotificationPreferencesService notificationPreferencesService;

    @InjectMocks
    private AccountSettingsService accountSettingsService;

    private MockedStatic<PrivacySettingsResponseFactory> privacyRespStatic;
    private MockedStatic<ReadingPreferencesResponseFactory> readingRespStatic;
    private MockedStatic<NotificationPreferencesResponseFactory> notifRespStatic;

    @BeforeEach
    void setUp() {
        privacyRespStatic = Mockito.mockStatic(PrivacySettingsResponseFactory.class);
        readingRespStatic = Mockito.mockStatic(ReadingPreferencesResponseFactory.class);
        notifRespStatic = Mockito.mockStatic(NotificationPreferencesResponseFactory.class);
    }

    @AfterEach
    void tearDown() {
        if (privacyRespStatic != null) privacyRespStatic.close();
        if (readingRespStatic != null) readingRespStatic.close();
        if (notifRespStatic != null) notifRespStatic.close();
    }

    @Test
    @DisplayName("Should aggregate preferences from all services on getAllSettings")
    void shouldAggregatePreferencesFromAllServicesOnGetAllSettings() {
        UUID userId = UUID.randomUUID();

        PrivacySettingsEntity privacy = PrivacySettingsEntity.builder().userId(userId).build();
        ReadingPreferencesEntity reading = ReadingPreferencesEntity.builder().userId(userId).build();
        NotificationPreferenceEntity notif = NotificationPreferenceEntity.builder().userId(userId).build();

        when(privacySettingsService.getUserPrivacySettings(userId)).thenReturn(privacy);
        when(readingPreferencesService.getUserReadingPreferences(userId)).thenReturn(reading);
        when(notificationPreferencesService.getUserPreferences(userId)).thenReturn(notif);

        PrivacySettingsResponse privacyDto = PrivacySettingsResponse.builder()
                .profileVisibility("public").showEmail(true).build();
        ReadingPreferencesResponse readingDto = ReadingPreferencesResponse.builder()
                .defaultLanguage("en").readingSpeed("normal").build();
        NotificationPreferencesResponse notifDto = NotificationPreferencesResponse.builder()
                .emailNotifications(true).pushNotifications(true).build();

        privacyRespStatic.when(() -> PrivacySettingsResponseFactory.createFromEntity(privacy)).thenReturn(privacyDto);
        readingRespStatic.when(() -> ReadingPreferencesResponseFactory.createFromEntity(reading)).thenReturn(readingDto);
        notifRespStatic.when(() -> NotificationPreferencesResponseFactory.createFromEntity(notif)).thenReturn(notifDto);

        AccountSettingsResponse result = accountSettingsService.getAllSettings(userId);

        assertThat(result).isNotNull();
        assertThat(result.getPrivacy()).isEqualTo(privacyDto);
        assertThat(result.getReading()).isEqualTo(readingDto);
        assertThat(result.getNotifications()).isEqualTo(notifDto);

        verify(privacySettingsService).getUserPrivacySettings(userId);
        verify(readingPreferencesService).getUserReadingPreferences(userId);
        verify(notificationPreferencesService).getUserPreferences(userId);
        privacyRespStatic.verify(() -> PrivacySettingsResponseFactory.createFromEntity(privacy));
        readingRespStatic.verify(() -> ReadingPreferencesResponseFactory.createFromEntity(reading));
        notifRespStatic.verify(() -> NotificationPreferencesResponseFactory.createFromEntity(notif));
    }

    @Test
    @DisplayName("Should update all settings when request contains all sections")
    void shouldUpdateAllSettingsWhenRequestContainsAllSections() {
        UUID userId = UUID.randomUUID();
        AccountSettingsUpdateRequest request = AccountSettingsUpdateRequest.builder()
                .privacy(PrivacySettingsUpdateRequest.builder().build())
                .reading(ReadingPreferencesUpdateRequest.builder().build())
                .notifications(NotificationPreferencesUpdateRequest.builder().build())
                .build();

        PrivacySettingsEntity updatedPrivacy = PrivacySettingsEntity.builder().userId(userId).build();
        ReadingPreferencesEntity updatedReading = ReadingPreferencesEntity.builder().userId(userId).build();
        NotificationPreferenceEntity updatedNotif = NotificationPreferenceEntity.builder().userId(userId).build();

        when(privacySettingsService.updatePrivacySettings(userId, request.getPrivacy())).thenReturn(updatedPrivacy);
        when(readingPreferencesService.updateReadingPreferences(userId, request.getReading())).thenReturn(updatedReading);
        when(notificationPreferencesService.updatePreferences(userId, request.getNotifications())).thenReturn(updatedNotif);

        PrivacySettingsResponse privacyDto = PrivacySettingsResponse.builder().profileVisibility("private").build();
        ReadingPreferencesResponse readingDto = ReadingPreferencesResponse.builder().defaultLanguage("tr").build();
        NotificationPreferencesResponse notifDto = NotificationPreferencesResponse.builder().emailNotifications(false).build();

        privacyRespStatic.when(() -> PrivacySettingsResponseFactory.createFromEntity(updatedPrivacy)).thenReturn(privacyDto);
        readingRespStatic.when(() -> ReadingPreferencesResponseFactory.createFromEntity(updatedReading)).thenReturn(readingDto);
        notifRespStatic.when(() -> NotificationPreferencesResponseFactory.createFromEntity(updatedNotif)).thenReturn(notifDto);

        AccountSettingsResponse result = accountSettingsService.updateAllSettings(userId, request);

        assertThat(result.getPrivacy()).isEqualTo(privacyDto);
        assertThat(result.getReading()).isEqualTo(readingDto);
        assertThat(result.getNotifications()).isEqualTo(notifDto);

        // ensure no fallback getUser* calls when updates provided
        verify(privacySettingsService, never()).getUserPrivacySettings(userId);
        verify(readingPreferencesService, never()).getUserReadingPreferences(userId);
        verify(notificationPreferencesService, never()).getUserPreferences(userId);
    }

    @Test
    @DisplayName("Should update only privacy and fallback to current reading and notifications")
    void shouldUpdateOnlyPrivacyAndFallbackToCurrentReadingAndNotifications() {
        UUID userId = UUID.randomUUID();
        PrivacySettingsUpdateRequest privacyReq = PrivacySettingsUpdateRequest.builder().build();
        AccountSettingsUpdateRequest request = AccountSettingsUpdateRequest.builder()
                .privacy(privacyReq)
                .build();

        PrivacySettingsEntity updatedPrivacy = PrivacySettingsEntity.builder().userId(userId).build();
        ReadingPreferencesEntity currentReading = ReadingPreferencesEntity.builder().userId(userId).build();
        NotificationPreferenceEntity currentNotif = NotificationPreferenceEntity.builder().userId(userId).build();

        when(privacySettingsService.updatePrivacySettings(userId, privacyReq)).thenReturn(updatedPrivacy);
        when(readingPreferencesService.getUserReadingPreferences(userId)).thenReturn(currentReading);
        when(notificationPreferencesService.getUserPreferences(userId)).thenReturn(currentNotif);

        PrivacySettingsResponse privacyDto = PrivacySettingsResponse.builder().profileVisibility("followers").build();
        ReadingPreferencesResponse readingDto = ReadingPreferencesResponse.builder().defaultLanguage("en").build();
        NotificationPreferencesResponse notifDto = NotificationPreferencesResponse.builder().emailNotifications(true).build();

        privacyRespStatic.when(() -> PrivacySettingsResponseFactory.createFromEntity(updatedPrivacy)).thenReturn(privacyDto);
        readingRespStatic.when(() -> ReadingPreferencesResponseFactory.createFromEntity(currentReading)).thenReturn(readingDto);
        notifRespStatic.when(() -> NotificationPreferencesResponseFactory.createFromEntity(currentNotif)).thenReturn(notifDto);

        AccountSettingsResponse result = accountSettingsService.updateAllSettings(userId, request);

        assertThat(result.getPrivacy()).isEqualTo(privacyDto);
        assertThat(result.getReading()).isEqualTo(readingDto);
        assertThat(result.getNotifications()).isEqualTo(notifDto);

        verify(readingPreferencesService).getUserReadingPreferences(userId);
        verify(notificationPreferencesService).getUserPreferences(userId);
        verify(readingPreferencesService, never()).updateReadingPreferences(eq(userId), any());
        verify(notificationPreferencesService, never()).updatePreferences(eq(userId), any());
    }

    @Test
    @DisplayName("Should update only reading and fallback to current privacy and notifications")
    void shouldUpdateOnlyReadingAndFallbackToCurrentPrivacyAndNotifications() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesUpdateRequest readingReq = ReadingPreferencesUpdateRequest.builder().build();
        AccountSettingsUpdateRequest request = AccountSettingsUpdateRequest.builder()
                .reading(readingReq)
                .build();

        ReadingPreferencesEntity updatedReading = ReadingPreferencesEntity.builder().userId(userId).build();
        PrivacySettingsEntity currentPrivacy = PrivacySettingsEntity.builder().userId(userId).build();
        NotificationPreferenceEntity currentNotif = NotificationPreferenceEntity.builder().userId(userId).build();

        when(readingPreferencesService.updateReadingPreferences(userId, readingReq)).thenReturn(updatedReading);
        when(privacySettingsService.getUserPrivacySettings(userId)).thenReturn(currentPrivacy);
        when(notificationPreferencesService.getUserPreferences(userId)).thenReturn(currentNotif);

        ReadingPreferencesResponse readingDto = ReadingPreferencesResponse.builder().defaultLanguage("es").build();
        PrivacySettingsResponse privacyDto = PrivacySettingsResponse.builder().profileVisibility("public").build();
        NotificationPreferencesResponse notifDto = NotificationPreferencesResponse.builder().pushNotifications(true).build();

        readingRespStatic.when(() -> ReadingPreferencesResponseFactory.createFromEntity(updatedReading)).thenReturn(readingDto);
        privacyRespStatic.when(() -> PrivacySettingsResponseFactory.createFromEntity(currentPrivacy)).thenReturn(privacyDto);
        notifRespStatic.when(() -> NotificationPreferencesResponseFactory.createFromEntity(currentNotif)).thenReturn(notifDto);

        AccountSettingsResponse result = accountSettingsService.updateAllSettings(userId, request);

        assertThat(result.getPrivacy()).isEqualTo(privacyDto);
        assertThat(result.getReading()).isEqualTo(readingDto);
        assertThat(result.getNotifications()).isEqualTo(notifDto);

        verify(privacySettingsService).getUserPrivacySettings(userId);
        verify(notificationPreferencesService).getUserPreferences(userId);
        verify(privacySettingsService, never()).updatePrivacySettings(eq(userId), any());
        verify(notificationPreferencesService, never()).updatePreferences(eq(userId), any());
    }

    @Test
    @DisplayName("Should update only notifications and fallback to current privacy and reading")
    void shouldUpdateOnlyNotificationsAndFallbackToCurrentPrivacyAndReading() {
        UUID userId = UUID.randomUUID();
        NotificationPreferencesUpdateRequest notifReq = NotificationPreferencesUpdateRequest.builder().build();
        AccountSettingsUpdateRequest request = AccountSettingsUpdateRequest.builder()
                .notifications(notifReq)
                .build();

        NotificationPreferenceEntity updatedNotif = NotificationPreferenceEntity.builder().userId(userId).build();
        PrivacySettingsEntity currentPrivacy = PrivacySettingsEntity.builder().userId(userId).build();
        ReadingPreferencesEntity currentReading = ReadingPreferencesEntity.builder().userId(userId).build();

        when(notificationPreferencesService.updatePreferences(userId, notifReq)).thenReturn(updatedNotif);
        when(privacySettingsService.getUserPrivacySettings(userId)).thenReturn(currentPrivacy);
        when(readingPreferencesService.getUserReadingPreferences(userId)).thenReturn(currentReading);

        NotificationPreferencesResponse notifDto = NotificationPreferencesResponse.builder().weeklyDigest(true).build();
        PrivacySettingsResponse privacyDto = PrivacySettingsResponse.builder().profileVisibility("private").build();
        ReadingPreferencesResponse readingDto = ReadingPreferencesResponse.builder().defaultLanguage("de").build();

        notifRespStatic.when(() -> NotificationPreferencesResponseFactory.createFromEntity(updatedNotif)).thenReturn(notifDto);
        privacyRespStatic.when(() -> PrivacySettingsResponseFactory.createFromEntity(currentPrivacy)).thenReturn(privacyDto);
        readingRespStatic.when(() -> ReadingPreferencesResponseFactory.createFromEntity(currentReading)).thenReturn(readingDto);

        AccountSettingsResponse result = accountSettingsService.updateAllSettings(userId, request);

        assertThat(result.getPrivacy()).isEqualTo(privacyDto);
        assertThat(result.getReading()).isEqualTo(readingDto);
        assertThat(result.getNotifications()).isEqualTo(notifDto);

        verify(privacySettingsService).getUserPrivacySettings(userId);
        verify(readingPreferencesService).getUserReadingPreferences(userId);
        verify(privacySettingsService, never()).updatePrivacySettings(eq(userId), any());
        verify(readingPreferencesService, never()).updateReadingPreferences(eq(userId), any());
    }
}

