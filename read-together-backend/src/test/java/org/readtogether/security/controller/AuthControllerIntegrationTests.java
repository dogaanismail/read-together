package org.readtogether.security.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.security.fixtures.KeyFixtures;
import org.readtogether.security.fixtures.TokenFixtures;
import org.readtogether.security.fixtures.TokenRequestFixtures;
import org.readtogether.security.model.request.TokenRefreshRequest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.PrivateKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final PrivateKey testPrivateKey = KeyFixtures.generateTestRsaKeyPair().getPrivate();

    @Test
    @DisplayName("POST /api/v1/auth/validate-token should return 200 for valid token")
    @Disabled("Fix later, probably check @PreAuthorize annotation")
    void shouldValidateValidToken() throws Exception {
        // Given: register and login to get a valid token
        String email = "auth.validate@test.local";
        String password = "Password1!";
        String accessToken = registerAndLoginUser(email, password);

        // When / Then
        mockMvc.perform(post("/api/v1/auth/validate-token")
                        .param("token", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/auth/validate-token should return 401 for invalid token")
    void shouldReturn401ForInvalidToken() throws Exception {
        // Given
        String invalidToken = TokenFixtures.createMalformedToken();

        // When / Then
        mockMvc.perform(post("/api/v1/auth/validate-token")
                        .param("token", invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/auth/validate-token should return 401 for expired token")
    void shouldReturn401ForExpiredToken() throws Exception {
        // Given
        String expiredToken = TokenFixtures.createExpiredToken(testPrivateKey);

        // When / Then
        mockMvc.perform(post("/api/v1/auth/validate-token")
                        .param("token", expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh-token should return 200 with new tokens for valid refresh token")
    @Disabled("Fix later, probably check @PreAuthorize annotation")
    void shouldRefreshWithValidRefreshToken() throws Exception {
        // Given: register and login to get tokens
        String email = "auth.refresh.valid@test.local";
        String password = "Password1!";
        JsonNode loginResponse = registerAndLoginUserGetResponse(email, password);
        String refreshToken = loginResponse.path("response").path("refreshToken").asText();

        TokenRefreshRequest request = TokenRequestFixtures.createTokenRefreshRequest(refreshToken);

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.accessToken").exists())
                .andExpect(jsonPath("$.response.refreshToken").exists())
                .andExpect(jsonPath("$.response.accessTokenExpiresAt").exists())
                .andReturn();

        // Then: verify response structure
        JsonNode responseNode = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(responseNode.path("response").path("accessToken").asText()).isNotBlank();
        assertThat(responseNode.path("response").path("refreshToken").asText()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh-token should return 401 for expired refresh token")
    void shouldReturn401ForExpiredRefreshToken() throws Exception {
        // Given
        String expiredRefreshToken = TokenFixtures.createExpiredToken(testPrivateKey);
        TokenRefreshRequest request = TokenRequestFixtures.createTokenRefreshRequest(expiredRefreshToken);

        // When / Then
        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh-token should return 401 for invalid refresh token")
    void shouldReturn401ForInvalidRefreshToken() throws Exception {
        // Given
        String invalidRefreshToken = TokenFixtures.createMalformedToken();
        TokenRefreshRequest request = TokenRequestFixtures.createTokenRefreshRequest(invalidRefreshToken);

        // When / Then
        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/auth/authenticate should return 200 with authentication for valid token")
    @Disabled("Fix later, probably check @PreAuthorize annotation")
    void shouldGetAuthenticationForValidToken() throws Exception {
        // Given: register and login to get a valid token
        String email = "auth.authenticate@test.local";
        String password = "Password1!";
        String accessToken = registerAndLoginUser(email, password);

        // When / Then
        mockMvc.perform(get("/api/v1/auth/authenticate")
                        .param("token", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.authorities").exists())
                .andExpect(jsonPath("$.principal").exists());
    }

    @Test
    @DisplayName("GET /api/v1/auth/authenticate should return 401 for invalid token")
    @Disabled("Fix later")
    void shouldReturn401ForInvalidTokenAuthentication() throws Exception {
        // Given
        String invalidToken = TokenFixtures.createMalformedToken();

        // When / Then
        mockMvc.perform(get("/api/v1/auth/authenticate")
                        .param("token", invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/auth/authenticate should return 401 without token")
    @Disabled("Fix later")
    void shouldReturn401WithoutToken() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/auth/authenticate"))
                .andExpect(status().isBadRequest()); // Missing required parameter
    }

    @Test
    @DisplayName("POST /api/v1/auth/validate-token should return 400 without token parameter")
    @Disabled("Fix later")
    void shouldReturn400WithoutTokenParameter() throws Exception {
        // When / Then
        mockMvc.perform(post("/api/v1/auth/validate-token"))
                .andExpect(status().isBadRequest()); // Missing required parameter
    }

    private String registerAndLoginUser(
            String email,
            String password) throws Exception {

        JsonNode loginResponse = registerAndLoginUserGetResponse(email, password);
        return loginResponse.path("response").path("accessToken").asText();
    }

    private JsonNode registerAndLoginUserGetResponse(
            String email,
            String password) throws Exception {

        // Register user
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                "Auth",
                "Test",
                "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        // Login user
        LoginRequest login = RequestFixtures.createLoginRequest(email, password);
        MvcResult loginResult = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(loginResult.getResponse().getContentAsString());
    }
}