package org.readtogether.user.factory;

import lombok.experimental.UtilityClass;

import org.readtogether.common.model.auth.dto.request.RegisterRequest;
import org.readtogether.common.model.auth.enums.UserType;
import org.readtogether.user.entity.UserEntity;

import static org.readtogether.common.model.auth.enums.UserType.ADMIN;
import static org.readtogether.common.model.auth.enums.UserType.USER;

@UtilityClass
public class UserEntityFactory {

    public static UserEntity getUserEntityByRegisterRequest(
            RegisterRequest registerRequest) {

        UserType userType = "admin".equalsIgnoreCase(registerRequest.getRole())
                ? ADMIN
                : USER;

        return UserEntity.builder()
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .userType(userType)
                .build();
    }
}
