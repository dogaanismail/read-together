package org.readtogether.readingroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomSettingsEntity;
import org.readtogether.readingroom.factory.ReadingRoomSettingsEntityFactory;
import org.readtogether.readingroom.factory.RoomSettingsResponseFactory;
import org.readtogether.readingroom.model.request.UpdateRoomSettingsRequest;
import org.readtogether.readingroom.model.response.RoomSettingsResponse;
import org.readtogether.readingroom.repository.ReadingRoomRepository;
import org.readtogether.readingroom.repository.ReadingRoomSettingsRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomSettingsService {

    private final ReadingRoomSettingsRepository settingsRepository;
    private final ReadingRoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RoomSettingsResponse updateRoomSettings(
            UUID roomId,
            UpdateRoomSettingsRequest request,
            UUID userId) {

        log.info("Updating settings for room {} by user {}", roomId, userId);

        ReadingRoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));

        // Only host can update settings
        if (!room.getHost().getId().equals(userId)) {
            throw new RuntimeException("Only the host can update room settings");
        }

        ReadingRoomSettingsEntity settings = settingsRepository.findByReadingRoomId(roomId)
                .orElseGet(() -> createDefaultSettingsEntity(room));

        updateSettingsFromRequest(settings, request);

        ReadingRoomSettingsEntity savedSettings = settingsRepository.save(settings);
        return mapToResponse(savedSettings);
    }

    @Transactional
    public RoomSettingsResponse getRoomSettings(UUID roomId) {

        ReadingRoomSettingsEntity settings = settingsRepository.findByReadingRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Settings not found for room: " + roomId));

        return mapToResponse(settings);
    }

    @Transactional
    public boolean validateRoomPassword(
            UUID roomId,
            String password) {

        if (!StringUtils.hasText(password)) {
            return false;
        }

        ReadingRoomSettingsEntity settings = settingsRepository.findByReadingRoomId(roomId)
                .orElse(null);

        if (settings == null || !StringUtils.hasText(settings.getPassword())) {
            return false;
        }

        return passwordEncoder.matches(password, settings.getPassword());
    }

    @Transactional
    public void createDefaultSettings(ReadingRoomEntity room) {

        ReadingRoomSettingsEntity settings = ReadingRoomSettingsEntityFactory.createDefaultSettings(room);
        ReadingRoomSettingsEntity savedSettings = settingsRepository.save(settings);
        RoomSettingsResponseFactory.createResponse(savedSettings);
    }

    public ReadingRoomSettingsEntity getSettingsEntity(UUID roomId) {

        return settingsRepository.findByReadingRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Settings not found for room: " + roomId));
    }

    private ReadingRoomSettingsEntity createDefaultSettingsEntity(ReadingRoomEntity room) {

        return ReadingRoomSettingsEntityFactory.createDefaultSettings(room);
    }

    private void updateSettingsFromRequest(

            ReadingRoomSettingsEntity settings,
            UpdateRoomSettingsRequest request) {

        if (request.getIsPublic() != null) {
            settings.setPublic(request.getIsPublic());
        }

        if (StringUtils.hasText(request.getPassword())) {
            settings.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRequireHostApproval() != null) {
            settings.setRequireHostApproval(request.getRequireHostApproval());
        }

        if (request.getEnableVideo() != null) {
            settings.setEnableVideo(request.getEnableVideo());
        }

        if (request.getEnableAudio() != null) {
            settings.setEnableAudio(request.getEnableAudio());
        }

        if (request.getEnableChat() != null) {
            settings.setEnableChat(request.getEnableChat());
        }

        if (request.getAllowRecording() != null) {
            settings.setAllowRecording(request.getAllowRecording());
        }

        if (request.getAutoMuteNewJoiners() != null) {
            settings.setAutoMuteNewJoiners(request.getAutoMuteNewJoiners());
        }

        if (request.getRoomVolume() != null) {
            settings.setRoomVolume(request.getRoomVolume());
        }

        if (request.getEnableLiveTranscription() != null) {
            settings.setEnableLiveTranscription(request.getEnableLiveTranscription());
        }

        if (request.getTranscriptionLanguage() != null) {
            settings.setTranscriptionLanguage(request.getTranscriptionLanguage());
        }

        if (request.getEnableSpeakerIdentification() != null) {
            settings.setEnableSpeakerIdentification(request.getEnableSpeakerIdentification());
        }

        if (request.getDownloadableTranscripts() != null) {
            settings.setDownloadableTranscripts(request.getDownloadableTranscripts());
        }

        if (request.getPronunciationHelp() != null) {
            settings.setPronunciationHelp(request.getPronunciationHelp());
        }
    }

    private RoomSettingsResponse mapToResponse(ReadingRoomSettingsEntity settings) {
        return RoomSettingsResponseFactory.createResponse(settings);
    }
}
