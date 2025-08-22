package org.readtogether.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.readtogether.security.common.enums.TokenClaims.USER_EMAIL;
import static org.readtogether.security.common.enums.TokenClaims.USER_ID;
import static org.readtogether.user.common.enums.UserType.ANONYMOUS;

@UtilityClass
public class SecurityUtils {

    public static UUID getCurrentUserId(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getClaimAsString(USER_ID.getValue()));
    }

    public static String getCurrentUserEmail() {

        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(user -> !ANONYMOUS.equals(user))
                .map(Jwt.class::cast)
                .map(jwt -> jwt.getClaim(USER_EMAIL.getValue()).toString())
                .orElse(ANONYMOUS.name());
    }

}

