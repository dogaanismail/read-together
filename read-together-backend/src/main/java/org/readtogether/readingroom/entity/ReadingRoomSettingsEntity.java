package org.readtogether.readingroom.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;
import org.readtogether.readingroom.common.enums.TranscriptionLanguage;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reading_room_settings")
public class ReadingRoomSettingsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_room_id", nullable = false)
    private ReadingRoomEntity readingRoom;

    @Column(name = "is_public")
    @Builder.Default
    private boolean isPublic = true;

    @Column(name = "password")
    private String password;

    @Column(name = "require_host_approval")
    @Builder.Default
    private boolean requireHostApproval = false;

    @Column(name = "enable_video")
    @Builder.Default
    private boolean enableVideo = true;

    @Column(name = "enable_audio")
    @Builder.Default
    private boolean enableAudio = true;

    @Column(name = "enable_chat")
    @Builder.Default
    private boolean enableChat = true;

    @Column(name = "allow_recording")
    @Builder.Default
    private boolean allowRecording = false;

    @Column(name = "auto_mute_new_joiners")
    @Builder.Default
    private boolean autoMuteNewJoiners = true;

    @Column(name = "room_volume")
    @Builder.Default
    private Integer roomVolume = 80;

    @Column(name = "enable_live_transcription")
    @Builder.Default
    private boolean enableLiveTranscription = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "transcription_language")
    @Builder.Default
    private TranscriptionLanguage transcriptionLanguage = TranscriptionLanguage.ENGLISH;

    @Column(name = "enable_speaker_identification")
    @Builder.Default
    private boolean enableSpeakerIdentification = true;

    @Column(name = "downloadable_transcripts")
    @Builder.Default
    private boolean downloadableTranscripts = true;

    @Column(name = "pronunciation_help")
    @Builder.Default
    private boolean pronunciationHelp = true;
}
