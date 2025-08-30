package org.readtogether.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.security.model.Token;
import org.readtogether.security.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomBearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    public void doFilterInternal(
            @NonNull final HttpServletRequest httpServletRequest,
            @NonNull final HttpServletResponse httpServletResponse,
            @NonNull final FilterChain filterChain) throws ServletException, IOException {

        log.debug("Api request has been secured by Bearer token");

        String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (Token.isBearerToken(authorizationHeader)) {
            try {
                String jwtToken = Token.getJwt(authorizationHeader);
                UsernamePasswordAuthenticationToken authentication = tokenService
                        .getAuthentication(jwtToken);

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            } catch (Exception e) {
                log.debug("Failed to authenticate JWT token: {}", e.getMessage());
                // Do not set authentication in SecurityContext, let it remain null
                // This will cause the request to be processed as unauthenticated
                // and handled by the AuthenticationEntryPoint
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

}