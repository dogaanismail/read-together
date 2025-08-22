package org.readtogether.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.readtogether.user.common.enums.FontSize;
import org.readtogether.user.common.enums.ReadingSpeed;
import org.readtogether.user.common.enums.Theme;
import org.readtogether.user.common.enums.VideoQuality;
import org.readtogether.user.entity.ReadingPreferencesEntity;
import org.readtogether.user.factory.ReadingPreferencesFactory;
import org.readtogether.user.model.request.ReadingPreferencesUpdateRequest;
import org.readtogether.user.repository.ReadingPreferencesRepository;
import org.readtogether.user.utils.ReadingPreferencesUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReadingPreferencesService Tests")
class ReadingPreferencesServiceTests {

    @Mock
    private ReadingPreferencesRepository readingPreferencesRepository;

    @InjectMocks
    private ReadingPreferencesService readingPreferencesService;

    private MockedStatic<ReadingPreferencesFactory> factoryStaticMock;
    private MockedStatic<ReadingPreferencesUtils> utilsStaticMock;

    @BeforeEach
    void setUp() {
        factoryStaticMock = Mockito.mockStatic(ReadingPreferencesFactory.class);
        utilsStaticMock = Mockito.mockStatic(ReadingPreferencesUtils.class);
    }

    @AfterEach
    void tearDown() {
        if (factoryStaticMock != null) factoryStaticMock.close();
        if (utilsStaticMock != null) utilsStaticMock.close();
    }

    @Test
    @DisplayName("Should return existing reading preferences when found")
    void shouldReturnExistingReadingPreferencesWhenFound() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity existing = ReadingPreferencesEntity.builder()
                .userId(userId)
                .readingSpeed(ReadingSpeed.NORMAL)
                .subtitlesEnabled(true)
                .autoplay(false)
                .videoQuality(VideoQuality.HIGH)
                .fontSize(FontSize.MEDIUM)
                .theme(Theme.LIGHT)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        ReadingPreferencesEntity result = readingPreferencesService.getUserReadingPreferences(userId);

        assertThat(result).isSameAs(existing);
        verify(readingPreferencesRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Should return default reading preferences when none exists")
    void shouldReturnDefaultReadingPreferencesWhenNoneExists() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity defaults = ReadingPreferencesEntity.builder()
                .userId(userId)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        factoryStaticMock.when(() -> ReadingPreferencesFactory.createDefaultPreferences(userId)).thenReturn(defaults);

        ReadingPreferencesEntity result = readingPreferencesService.getUserReadingPreferences(userId);

        assertThat(result).isSameAs(defaults);
        verify(readingPreferencesRepository).findByUserId(userId);
        factoryStaticMock.verify(() -> ReadingPreferencesFactory.createDefaultPreferences(userId));
    }

    @Test
    @DisplayName("Should update reading preferences when existing record found")
    void shouldUpdateReadingPreferencesWhenExistingFound() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesUpdateRequest request = ReadingPreferencesUpdateRequest.builder()
                .defaultLanguage(ReadingPreferencesEntity.Language.TURKISH)
                .readingSpeed(ReadingSpeed.FAST)
                .subtitlesEnabled(false)
                .autoplay(true)
                .videoQuality(VideoQuality.MEDIUM)
                .fontSize(FontSize.LARGE)
                .theme(Theme.DARK)
                .build();

        ReadingPreferencesEntity existing = ReadingPreferencesEntity.builder()
                .userId(userId)
                .defaultLanguage(ReadingPreferencesEntity.Language.ENGLISH)
                .readingSpeed(ReadingSpeed.NORMAL)
                .subtitlesEnabled(true)
                .autoplay(false)
                .videoQuality(VideoQuality.HIGH)
                .fontSize(FontSize.MEDIUM)
                .theme(Theme.LIGHT)
                .build();

        ReadingPreferencesEntity updates = ReadingPreferencesEntity.builder()
                .userId(userId)
                .defaultLanguage(ReadingPreferencesEntity.Language.TURKISH)
                .readingSpeed(ReadingSpeed.FAST)
                .subtitlesEnabled(false)
                .autoplay(true)
                .videoQuality(VideoQuality.MEDIUM)
                .fontSize(FontSize.LARGE)
                .theme(Theme.DARK)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(readingPreferencesRepository.save(any(ReadingPreferencesEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        factoryStaticMock.when(() -> ReadingPreferencesFactory.createFromDto(userId, request)).thenReturn(updates);

        ReadingPreferencesEntity result = readingPreferencesService.updateReadingPreferences(userId, request);

        utilsStaticMock.verify(() -> ReadingPreferencesUtils.applyUpdates(existing, updates));
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(readingPreferencesRepository).save(existing);
    }

    @Test
    @DisplayName("Should create and save reading preferences when none exists")
    void shouldCreateAndSaveReadingPreferencesWhenNoneExists() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesUpdateRequest request = ReadingPreferencesUpdateRequest.builder()
                .defaultLanguage(ReadingPreferencesEntity.Language.SPANISH)
                .readingSpeed(ReadingSpeed.SLOW)
                .subtitlesEnabled(true)
                .autoplay(false)
                .videoQuality(VideoQuality.LOW)
                .fontSize(FontSize.SMALL)
                .theme(Theme.LIGHT)
                .build();

        ReadingPreferencesEntity createdFirst = ReadingPreferencesEntity.builder()
                .userId(userId)
                .defaultLanguage(ReadingPreferencesEntity.Language.ENGLISH)
                .readingSpeed(ReadingSpeed.NORMAL)
                .subtitlesEnabled(true)
                .autoplay(false)
                .videoQuality(VideoQuality.HIGH)
                .fontSize(FontSize.MEDIUM)
                .theme(Theme.LIGHT)
                .build();

        ReadingPreferencesEntity createdSecond = ReadingPreferencesEntity.builder()
                .userId(userId)
                .defaultLanguage(ReadingPreferencesEntity.Language.SPANISH)
                .readingSpeed(ReadingSpeed.SLOW)
                .subtitlesEnabled(true)
                .autoplay(false)
                .videoQuality(VideoQuality.LOW)
                .fontSize(FontSize.SMALL)
                .theme(Theme.LIGHT)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(readingPreferencesRepository.save(any(ReadingPreferencesEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        factoryStaticMock.when(() -> ReadingPreferencesFactory.createFromDto(userId, request))
                .thenReturn(createdFirst, createdSecond);

        ReadingPreferencesEntity result = readingPreferencesService.updateReadingPreferences(userId, request);

        utilsStaticMock.verify(() -> ReadingPreferencesUtils.applyUpdates(createdFirst, createdSecond));
        assertThat(result).isSameAs(createdFirst);
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(readingPreferencesRepository).save(createdFirst);
    }

    @Test
    @DisplayName("Should return correct speed multiplier based on preferences")
    void shouldReturnCorrectSpeedMultiplierBasedOnPreferences() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity prefs = ReadingPreferencesEntity.builder()
                .userId(userId)
                .readingSpeed(ReadingSpeed.FAST)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.of(prefs));
        utilsStaticMock.when(() -> ReadingPreferencesUtils.getSpeedMultiplier(ReadingSpeed.FAST))
                .thenReturn(1.25);

        double result = readingPreferencesService.getSpeedMultiplier(userId);
        assertThat(result).isEqualTo(1.25);
    }

    @Test
    @DisplayName("Should return correct speed multiplier when preferences missing (fallback)")
    void shouldReturnCorrectSpeedMultiplierWhenPreferencesMissing() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity defaults = ReadingPreferencesEntity.builder()
                .userId(userId)
                .readingSpeed(ReadingSpeed.SLOW)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        factoryStaticMock.when(() -> ReadingPreferencesFactory.createDefaultPreferences(userId)).thenReturn(defaults);
        utilsStaticMock.when(() -> ReadingPreferencesUtils.getSpeedMultiplier(ReadingSpeed.SLOW))
                .thenReturn(0.75);

        double result = readingPreferencesService.getSpeedMultiplier(userId);
        assertThat(result).isEqualTo(0.75);
    }

    @Test
    @DisplayName("Should return correct video quality resolution based on preferences")
    void shouldReturnCorrectVideoQualityResolutionBasedOnPreferences() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity prefs = ReadingPreferencesEntity.builder()
                .userId(userId)
                .videoQuality(VideoQuality.MEDIUM)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.of(prefs));
        utilsStaticMock.when(() -> ReadingPreferencesUtils.getVideoQualityResolution(VideoQuality.MEDIUM))
                .thenReturn("720p");

        String result = readingPreferencesService.getVideoQualityResolution(userId);
        assertThat(result).isEqualTo("720p");
    }

    @Test
    @DisplayName("Should return correct video quality resolution when preferences missing (fallback)")
    void shouldReturnCorrectVideoQualityResolutionWhenPreferencesMissing() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity defaults = ReadingPreferencesEntity.builder()
                .userId(userId)
                .videoQuality(VideoQuality.HIGH)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        factoryStaticMock.when(() -> ReadingPreferencesFactory.createDefaultPreferences(userId)).thenReturn(defaults);
        utilsStaticMock.when(() -> ReadingPreferencesUtils.getVideoQualityResolution(VideoQuality.HIGH))
                .thenReturn("1080p");

        String result = readingPreferencesService.getVideoQualityResolution(userId);
        assertThat(result).isEqualTo("1080p");
    }

    @Test
    @DisplayName("Should return language code based on preferences")
    void shouldReturnLanguageCodeBasedOnPreferences() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity prefs = ReadingPreferencesEntity.builder()
                .userId(userId)
                .defaultLanguage(ReadingPreferencesEntity.Language.FRENCH)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.of(prefs));
        utilsStaticMock.when(() -> ReadingPreferencesUtils.getLanguageCode(ReadingPreferencesEntity.Language.FRENCH))
                .thenReturn("fr");

        String result = readingPreferencesService.getLanguageCode(userId);
        assertThat(result).isEqualTo("fr");
    }

    @Test
    @DisplayName("Should return language code when preferences missing (fallback)")
    void shouldReturnLanguageCodeWhenPreferencesMissing() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity defaults = ReadingPreferencesEntity.builder()
                .userId(userId)
                .defaultLanguage(ReadingPreferencesEntity.Language.ENGLISH)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        factoryStaticMock.when(() -> ReadingPreferencesFactory.createDefaultPreferences(userId)).thenReturn(defaults);
        utilsStaticMock.when(() -> ReadingPreferencesUtils.getLanguageCode(ReadingPreferencesEntity.Language.ENGLISH))
                .thenReturn("en");

        String result = readingPreferencesService.getLanguageCode(userId);
        assertThat(result).isEqualTo("en");
    }

    @Test
    @DisplayName("Should return subtitlesEnabled flag from existing preferences")
    void shouldReturnSubtitlesEnabledFromExistingPreferences() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity prefs = ReadingPreferencesEntity.builder()
                .userId(userId)
                .subtitlesEnabled(false)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.of(prefs));

        assertThat(readingPreferencesService.shouldEnableSubtitles(userId)).isFalse();
    }

    @Test
    @DisplayName("Should fallback to default subtitlesEnabled when preferences missing (true)")
    void shouldFallbackToDefaultSubtitlesEnabledWhenMissing() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity defaults = ReadingPreferencesEntity.builder()
                .userId(userId)
                .subtitlesEnabled(true)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        factoryStaticMock.when(() -> ReadingPreferencesFactory.createDefaultPreferences(userId)).thenReturn(defaults);

        assertThat(readingPreferencesService.shouldEnableSubtitles(userId)).isTrue();
    }

    @Test
    @DisplayName("Should return autoplay flag from existing preferences")
    void shouldReturnAutoplayFromExistingPreferences() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity prefs = ReadingPreferencesEntity.builder()
                .userId(userId)
                .autoplay(true)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.of(prefs));

        assertThat(readingPreferencesService.shouldAutoplay(userId)).isTrue();
    }

    @Test
    @DisplayName("Should fallback to default autoplay when preferences missing (false)")
    void shouldFallbackToDefaultAutoplayWhenMissing() {
        UUID userId = UUID.randomUUID();
        ReadingPreferencesEntity defaults = ReadingPreferencesEntity.builder()
                .userId(userId)
                .autoplay(false)
                .build();

        when(readingPreferencesRepository.findByUserId(userId)).thenReturn(Optional.empty());
        factoryStaticMock.when(() -> ReadingPreferencesFactory.createDefaultPreferences(userId)).thenReturn(defaults);

        assertThat(readingPreferencesService.shouldAutoplay(userId)).isFalse();
    }
}

