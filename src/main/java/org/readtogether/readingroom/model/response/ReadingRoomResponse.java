package org.readtogether.readingroom.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.readingroom.common.enums.RoomStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingRoomResponse {

    private UUID id;
    private String title;
    private String description;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Boolean isPublic;
    private String roomCode;
    private RoomStatus status;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime endTime;
    private HostInfo host;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HostInfo {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
    }

}
