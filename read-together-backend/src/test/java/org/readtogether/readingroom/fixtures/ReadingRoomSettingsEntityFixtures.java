package org.readtogether.readingroom.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.common.enums.TranscriptionLanguage;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomSettingsEntity;

import java.util.UUID;

import static org.readtogether.readingroom.common.enums.TranscriptionLanguage.*;

@UtilityClass
public class ReadingRoomSettingsEntityFixtures {

    public static ReadingRoomSettingsEntity createDefaultSettingsEntity(
            ReadingRoomEntity room) {

        return createSettingsEntity(
                room,
                true,
                false,
                true,
                true,
                true,
                true,
                null,
                80,
                false,
                ENGLISH,
                true,
                true,
                true
        );
    }

    public static ReadingRoomSettingsEntity createPrivateSettingsEntity(
            ReadingRoomEntity room) {

        return createSettingsEntity(
                room,
                false,
                true,
                true,
                true,
                true,
                false,
                "hashedPassword123",
                70,
                true,
                ENGLISH,
                true,
                false,
                true
        );
    }

    public static ReadingRoomSettingsEntity createRestrictiveSettingsEntity(
            ReadingRoomEntity room) {

        return createSettingsEntity(
                room,
                false,
                true,
                false,
                false,
                false,
                true,
                "secureHash456",
                50,
                false,
                SPANISH,
                false,
                false,
                false
        );
    }

    public static ReadingRoomSettingsEntity createSettingsEntity(
            ReadingRoomEntity room,
            boolean isPublic,
            boolean requireHostApproval,
            boolean enableChat,
            boolean enableAudio,
            boolean enableVideo,
            boolean autoMuteNewJoiners,
            String passwordHash,
            Integer roomVolume,
            boolean enableLiveTranscription,
            TranscriptionLanguage transcriptionLanguage,
            boolean enableSpeakerIdentification,
            boolean downloadableTranscripts,
            boolean pronunciationHelp) {

        return ReadingRoomSettingsEntity.builder()
                .id(UUID.randomUUID())
                .readingRoom(room)
                .isPublic(isPublic)
                .requireHostApproval(requireHostApproval)
                .enableChat(enableChat)
                .enableAudio(enableAudio)
                .enableVideo(enableVideo)
                .autoMuteNewJoiners(autoMuteNewJoiners)
                .password(passwordHash)
                .roomVolume(roomVolume)
                .enableLiveTranscription(enableLiveTranscription)
                .transcriptionLanguage(transcriptionLanguage)
                .enableSpeakerIdentification(enableSpeakerIdentification)
                .downloadableTranscripts(downloadableTranscripts)
                .pronunciationHelp(pronunciationHelp)
                .allowRecording(false)
                .build();
    }

    public static ReadingRoomSettingsEntity createPasswordProtectedSettings(
            ReadingRoomEntity room,
            String hashedPassword) {

        return createSettingsEntity(
                room,
                false,
                false,
                true,
                true,
                true,
                true,
                hashedPassword,
                80,
                false,
                ENGLISH,
                true,
                true,
                true
        );
    }

    public static ReadingRoomSettingsEntity createOpenSettings(
            ReadingRoomEntity room) {

        return createSettingsEntity(
                room,
                true,
                false,
                true,
                true,
                true,
                false,
                null,
                90,
                true,
                ENGLISH,
                true,
                true,
                true
        );
    }
}