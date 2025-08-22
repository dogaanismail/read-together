package org.readtogether.security.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.common.model.response.CustomResponse;
import org.readtogether.security.model.Token;
import org.readtogether.security.model.response.TokenResponse;
import org.readtogether.security.model.request.TokenRefreshRequest;
import org.readtogether.security.service.RefreshTokenService;
import org.readtogether.security.service.TokenService;
import org.readtogether.user.mapper.TokenToTokenResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;
    private final TokenToTokenResponseMapper tokenToTokenResponseMapper = TokenToTokenResponseMapper
            .initialize();

    @PostMapping("/validate-token")
    public ResponseEntity<Void> validateToken(
            @RequestParam(name = "token") String token) {

        log.info("Received a request to validate a token");
        tokenService.verifyAndValidate(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public CustomResponse<TokenResponse> refreshToken(
            @RequestBody @Valid TokenRefreshRequest tokenRefreshRequest) {

        log.info("Received a request to refresh a user's token");
        Token token = refreshTokenService.refreshToken(tokenRefreshRequest);
        TokenResponse tokenResponse = tokenToTokenResponseMapper.map(token);
        return CustomResponse.successOf(tokenResponse);
    }

    @GetMapping("/authenticate")
    public ResponseEntity<UsernamePasswordAuthenticationToken> getAuthentication(
            @RequestParam(name = "token") String token) {

        log.info("Received a request to get authentication for a token");
        UsernamePasswordAuthenticationToken authentication = tokenService.getAuthentication(token);
        return ResponseEntity.ok(authentication);
    }

}
