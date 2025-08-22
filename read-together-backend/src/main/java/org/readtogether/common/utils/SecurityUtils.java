package org.readtogether.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ANONYMOUS.name();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            Object email = jwt.getClaim(USER_EMAIL.getValue());
            return email != null ? email.toString() : ANONYMOUS.name();
        }

        if (principal instanceof UserDetails userDetails) {
            return Optional.ofNullable(userDetails.getUsername()).orElse(ANONYMOUS.name());
        }

        if (principal instanceof String s) {
            return (s.isBlank() || ANONYMOUS.name().equalsIgnoreCase(s)) ? ANONYMOUS.name() : s;
        }

        return ANONYMOUS.name();
    }

}
