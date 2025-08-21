package org.readtogether.readingroom.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.readingroom.common.enums.TranscriptionLanguage;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSettingsResponse {

    private UUID id;
    private UUID readingRoomId;

    // Privacy & Security
    private Boolean isPublic;
    private Boolean hasPassword;
    private Boolean requireHostApproval;

    // Media Controls
    private Boolean enableVideo;
    private Boolean enableAudio;
    private Boolean enableChat;
    private Boolean allowRecording;
    private Boolean autoMuteNewJoiners;
    private Integer roomVolume;

    // Live Transcription
    private Boolean enableLiveTranscription;
    private TranscriptionLanguage transcriptionLanguage;
    private String transcriptionLanguageDisplay;
    private Boolean enableSpeakerIdentification;
    private Boolean downloadableTranscripts;
    private Boolean pronunciationHelp;
}
