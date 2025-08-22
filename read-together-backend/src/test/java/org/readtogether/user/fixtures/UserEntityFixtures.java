package org.readtogether.user.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.common.enums.UserType;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class UserEntityFixtures {

    public static final UUID DEFAULT_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    public static final UUID SECONDARY_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

    public static UserEntity createDefaultUserEntity() {

        return createUserEntity(DEFAULT_USER_ID,
                "test@example.com",
                "John",
                "Doe",
                UserType.USER
        );
    }

    public static UserEntity createSecondaryUserEntity() {

        return createUserEntity(SECONDARY_USER_ID,
                "jane@example.com",
                "Jane",
                "Smith",
                UserType.USER
        );
    }

    public static UserEntity createUserEntity(
            UUID id,
            String email,
            String firstName,
            String lastName,
            UserType userType) {

        return UserEntity.builder()
                .id(id)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .userType(userType)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

}
