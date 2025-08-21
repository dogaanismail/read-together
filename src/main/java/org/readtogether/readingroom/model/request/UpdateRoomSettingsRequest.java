package org.readtogether.readingroom.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.readingroom.common.enums.TranscriptionLanguage;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomSettingsRequest {

    private Boolean isPublic;

    @Size(min = 4, max = 50, message = "Password must be between 4 and 50 characters")
    private String password;

    private Boolean requireHostApproval;

    private Boolean enableVideo;
    private Boolean enableAudio;
    private Boolean enableChat;
    private Boolean allowRecording;
    private Boolean autoMuteNewJoiners;

    @Min(value = 0, message = "Room volume cannot be negative")
    @Max(value = 100, message = "Room volume cannot exceed 100")
    private Integer roomVolume;

    private Boolean enableLiveTranscription;
    private TranscriptionLanguage transcriptionLanguage;
    private Boolean enableSpeakerIdentification;
    private Boolean downloadableTranscripts;
    private Boolean pronunciationHelp;
}
