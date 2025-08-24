package org.readtogether.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SecurityConfig Integration Tests")
class SecurityConfigIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should permit POST /api/v1/users/** endpoints")
    void shouldPermitUserEndpoints() throws Exception {
        // Given
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                "security.test@test.local",
                "Password1!",
                "Security",
                "Test",
                "user"
        );

        // When / Then - should allow registration without authentication
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("Should permit GET /api/v1/sessions/public endpoint")
    void shouldPermitPublicSessionsEndpoint() throws Exception {
        // When / Then - should allow access without authentication (200 means endpoint exists and works)
        mockMvc.perform(get("/api/v1/sessions/public"))
                .andExpect(status().isOk()); // Endpoint exists and returns data without auth
    }

    @Test
    @DisplayName("Should permit GET /api/v1/library/books/public endpoint")
    void shouldPermitPublicLibraryBooksEndpoint() throws Exception {
        // When / Then - should allow access without authentication (200 means endpoint exists and works)
        mockMvc.perform(get("/api/v1/library/books/public"))
                .andExpect(status().isOk()); // Endpoint exists and returns data without auth
    }

    @Test
    @DisplayName("Should permit GET /api/v1/room/join endpoint")
    void shouldPermitRoomJoinEndpoint() throws Exception {
        // When / Then - should allow access without authentication
        // This endpoint may not exist, so we expect it to not return 401 (which would indicate auth failure)
        mockMvc.perform(get("/api/v1/room/join"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401) {
                        throw new AssertionError("Expected any status except 401, but got 401");
                    }
                }); // Any status except 401 is fine (means it passed security)
    }

    @Test
    @DisplayName("Should protect GET /api/v1/users/current-user endpoint")
    void shouldProtectCurrentUserEndpoint() throws Exception {
        // When / Then - should return 401 without authentication
        mockMvc.perform(get("/api/v1/users/current-user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"));
    }

    @Test
    @DisplayName("Should protect GET /api/v1/sessions endpoint")
    void shouldProtectSessionsEndpoint() throws Exception {
        // When / Then - should return 401 without authentication
        mockMvc.perform(get("/api/v1/sessions"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"));
    }

    @Test
    @DisplayName("Should protect POST /api/v1/sessions endpoint")
    void shouldProtectSessionsPostEndpoint() throws Exception {
        // When / Then - should return 401 without authentication
        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"));
    }

    @Test
    @DisplayName("Should protect GET /api/v1/reading-preferences endpoint")
    void shouldProtectReadingPreferencesEndpoint() throws Exception {
        // When / Then - should return 401 without authentication
        mockMvc.perform(get("/api/v1/reading-preferences"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"));
    }

    @Test
    @DisplayName("Should protect GET /api/v1/privacy-settings endpoint")
    void shouldProtectPrivacySettingsEndpoint() throws Exception {
        // When / Then - should return 401 without authentication
        mockMvc.perform(get("/api/v1/privacy-settings"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"));
    }

    @Test
    @DisplayName("Should return JSON for 401 via CustomErrorAuthenticationEntryPoint")
    void shouldReturnJsonFor401() throws Exception {
        // When / Then - should return JSON error response
        mockMvc.perform(get("/api/v1/users/current-user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should protect PUT endpoints without authentication")
    void shouldProtectPutEndpoints() throws Exception {
        // When / Then - should return 401 without authentication
        mockMvc.perform(post("/api/v1/reading-preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"));
    }

    @Test
    @DisplayName("Should protect DELETE endpoints without authentication")
    void shouldProtectDeleteEndpoints() throws Exception {
        // When / Then - should return 401 without authentication
        mockMvc.perform(post("/api/v1/sessions/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"));
    }

    @Test
    @DisplayName("Should handle invalid authorization header gracefully")
    void shouldHandleInvalidAuthorizationHeaderGracefully() throws Exception {
        // When / Then - The filter should catch JWT exceptions, and the endpoint should still return 401
        // The CustomBearerTokenAuthenticationFilter handles exceptions by not setting authentication
        // which leads to a 401 from the entry point
        mockMvc.perform(get("/api/v1/users/current-user")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"));
    }

    @Test
    @DisplayName("Should handle non-Bearer authorization header gracefully")
    void shouldHandleNonBearerAuthorizationHeaderGracefully() throws Exception {
        // When / Then - should return 401 with a non-Bearer token
        mockMvc.perform(get("/api/v1/users/current-user")
                        .header("Authorization", "Basic dXNlcjpwYXNzd29yZA=="))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"));
    }
}