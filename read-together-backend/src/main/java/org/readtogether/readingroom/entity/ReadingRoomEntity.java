package org.readtogether.readingroom.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.entity.BaseEntity;
import org.readtogether.readingroom.common.enums.RoomStatus;
import org.readtogether.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.readtogether.readingroom.common.enums.RoomStatus.WAITING;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reading_room")
public class ReadingRoomEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "max_participants")
    @Builder.Default
    private int maxParticipants = 12;

    @Column(name = "is_public")
    @Builder.Default
    private boolean isPublic = true;

    @Column(name = "room_code", unique = true, nullable = false)
    private String roomCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private RoomStatus status = WAITING;

    @Column(name = "scheduled_start_time")
    private LocalDateTime scheduledStartTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private UserEntity host;

    @OneToMany(mappedBy = "readingRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReadingRoomParticipantEntity> participants = new ArrayList<>();

    @OneToOne(mappedBy = "readingRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ReadingRoomSettingsEntity settings;

}
