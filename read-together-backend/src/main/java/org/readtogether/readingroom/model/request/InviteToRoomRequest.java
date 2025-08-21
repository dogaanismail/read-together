package org.readtogether.readingroom.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.readtogether.readingroom.common.enums.InvitationType;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteToRoomRequest {

    @NotNull(message = "Invitation type is required")
    private InvitationType invitationType;

    @Email(message = "Invalid email format")
    private List<String> invitedEmails;

    private List<UUID> invitedUserIds;

    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;

    private Boolean generateNewToken;

    @Builder.Default
    private int expirationHours = 24;
}
