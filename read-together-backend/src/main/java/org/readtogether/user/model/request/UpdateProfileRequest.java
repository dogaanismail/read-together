package org.readtogether.user.model.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")  
    private String lastName;

    @Size(max = 200, message = "Bio cannot exceed 200 characters")
    private String bio;

    @Size(max = 500, message = "Profile picture URL cannot exceed 500 characters")
    private String profilePictureUrl;

    @Size(max = 50, message = "Username cannot exceed 50 characters")
    private String username;

}