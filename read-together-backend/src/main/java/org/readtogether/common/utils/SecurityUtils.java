package org.readtogether.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.readtogether.common.enums.TokenClaims.USER_ID;

@UtilityClass
public class SecurityUtils {

    public static UUID getCurrentUserId(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getClaimAsString(USER_ID.getValue()));
    }
}

