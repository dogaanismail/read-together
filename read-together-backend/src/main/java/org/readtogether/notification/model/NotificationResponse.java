package org.readtogether.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID id;
    private UUID userId;
    private UUID sessionId;
    private String type;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    // Additional fields expected by frontend
    private String userName;
    private String userAvatar;
    private String sessionTitle;
    private String metadata;
}
