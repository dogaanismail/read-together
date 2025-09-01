package org.readtogether.user.factory;

import lombok.experimental.UtilityClass;

import org.apache.commons.lang3.StringUtils;
import org.readtogether.user.model.request.RegisterRequest;
import org.readtogether.user.common.enums.UserType;
import org.readtogether.user.entity.UserEntity;

import static org.readtogether.user.common.enums.UserType.ADMIN;
import static org.readtogether.user.common.enums.UserType.USER;

@UtilityClass
public class UserEntityFactory {

    public static UserEntity getUserEntityByRegisterRequest(
            RegisterRequest registerRequest) {

        UserType userType = USER;
        if (StringUtils.isNotBlank(registerRequest.getRole())) {
            if (registerRequest.getRole().equalsIgnoreCase("admin")) {
                userType = ADMIN;
            }
        }

        return UserEntity.builder()
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .userType(userType)
                .build();
    }
}
