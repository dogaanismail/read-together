package org.readtogether.security.fixtures;

import lombok.experimental.UtilityClass;
import org.readtogether.user.common.enums.UserType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class SecurityContextFixtures {

    public static Authentication buildAuthentication(
            String userId,
            Set<UserType> authorities) {

        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                .map(userType -> new SimpleGrantedAuthority(userType.name()))
                .toList();

        Jwt jwt = new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("typ", "Bearer", "alg", "RS256"),
                Map.of("userId", userId, "userType", authorities.iterator().next().name())
        );

        return UsernamePasswordAuthenticationToken.authenticated(
                jwt,
                null,
                grantedAuthorities
        );
    }

    public static Authentication buildUserAuthentication(
            String userId) {

        return buildAuthentication(userId, Set.of(UserType.USER));
    }

    public static Authentication buildAdminAuthentication(
            String userId) {

        return buildAuthentication(userId, Set.of(UserType.ADMIN));
    }

    public static SecurityContext buildSecurityContext(
            Authentication authentication) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

    public static SecurityContext buildUserSecurityContext(
            String userId) {

        Authentication auth = buildUserAuthentication(userId);
        return buildSecurityContext(auth);
    }

    public static SecurityContext buildAdminSecurityContext(
            String userId) {

        Authentication auth = buildAdminAuthentication(userId);
        return buildSecurityContext(auth);
    }
}