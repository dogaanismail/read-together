package org.readtogether.readingroom.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomSettingsEntity;

import static org.readtogether.readingroom.common.enums.TranscriptionLanguage.ENGLISH;

@UtilityClass
public class ReadingRoomSettingsEntityFactory {

    public ReadingRoomSettingsEntity createDefaultSettings(
            ReadingRoomEntity readingRoom) {

        return ReadingRoomSettingsEntity.builder()
                .readingRoom(readingRoom)
                .isPublic(readingRoom.isPublic())
                .requireHostApproval(false)
                .enableVideo(true)
                .enableAudio(true)
                .enableChat(true)
                .allowRecording(false)
                .autoMuteNewJoiners(true)
                .roomVolume(80)
                .enableLiveTranscription(false)
                .transcriptionLanguage(ENGLISH)
                .enableSpeakerIdentification(true)
                .downloadableTranscripts(true)
                .pronunciationHelp(true)
                .build();
    }

}
