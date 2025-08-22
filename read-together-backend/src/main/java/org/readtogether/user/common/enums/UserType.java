package org.readtogether.user.common.enums;

import lombok.Getter;

@Getter
public enum UserType {

    USER("USER"),
    ADMIN("ADMIN"),
    ANONYMOUS("anonymousUser");

    private final String type;

    UserType(String type) {
        this.type = type;
    }
}
