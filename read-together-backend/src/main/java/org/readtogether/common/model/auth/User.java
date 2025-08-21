package org.readtogether.common.model.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.readtogether.common.model.auth.enums.UserStatus;
import org.readtogether.common.model.auth.enums.UserType;

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
    private UserType customerType;
}
