package org.readtogether.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.readtogether.user.common.enums.UserStatus;
import org.readtogether.user.common.enums.UserType;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class User {

    private String id;

    private String email;

    private String firstName;

    private String lastName;

    private UserStatus userStatus;

    private UserType userType;
}
