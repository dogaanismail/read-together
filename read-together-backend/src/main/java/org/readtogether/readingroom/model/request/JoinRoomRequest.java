package org.readtogether.readingroom.model.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRoomRequest {

    @Size(min = 4, max = 20, message = "Room code must be between 4 and 20 characters")
    private String roomCode;

    @Size(min = 4, max = 50, message = "Password must be between 4 and 50 characters")
    private String password;
}
