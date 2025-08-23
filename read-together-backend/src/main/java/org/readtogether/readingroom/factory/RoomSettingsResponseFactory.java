package org.readtogether.readingroom.factory;

import lombok.experimental.UtilityClass;
import org.readtogether.readingroom.entity.ReadingRoomSettingsEntity;
import org.readtogether.readingroom.model.response.RoomSettingsResponse;
import org.springframework.util.StringUtils;

@UtilityClass
public class RoomSettingsResponseFactory {

    public RoomSettingsResponse createResponse(
            ReadingRoomSettingsEntity settings) {

        return RoomSettingsResponse.builder()
                .id(settings.getId())
                .readingRoomId(settings.getReadingRoom().getId())
                .isPublic(settings.isPublic())
                .hasPassword(StringUtils.hasText(settings.getPassword()))
                .requireHostApproval(settings.isRequireHostApproval())
                .enableVideo(settings.isEnableVideo())
                .enableAudio(settings.isEnableAudio())
                .enableChat(settings.isEnableChat())
                .allowRecording(settings.isAllowRecording())
                .autoMuteNewJoiners(settings.isAutoMuteNewJoiners())
                .roomVolume(settings.getRoomVolume())
                .enableLiveTranscription(settings.isEnableLiveTranscription())
                .transcriptionLanguage(settings.getTranscriptionLanguage())
                .transcriptionLanguageDisplay(settings.getTranscriptionLanguage().getDisplayName())
                .enableSpeakerIdentification(settings.isEnableSpeakerIdentification())
                .downloadableTranscripts(settings.isDownloadableTranscripts())
                .pronunciationHelp(settings.isPronunciationHelp())
                .build();
    }
}
