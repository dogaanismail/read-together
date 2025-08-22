package org.readtogether.session.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.session.entity.SessionEntity;
import org.readtogether.session.fixtures.SessionEntityFixtures;
import org.readtogether.session.repository.SessionRepository;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    @DisplayName("GET /api/v1/sessions/{id} should return session by id")
    void shouldGetSessionById() throws Exception {
        // Given
        String accessToken = registerAndLoginUser("session.reader@test.local", "Password1!");
        SessionEntity savedSession = createSessionInDatabase();

        // When / Then
        mockMvc.perform(get("/api/v1/sessions/{id}", savedSession.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedSession.getId().toString()))
                .andExpect(jsonPath("$.title").value(savedSession.getTitle()))
                .andExpect(jsonPath("$.mediaType").value(savedSession.getMediaType().toString()));
    }

    @Test
    @DisplayName("GET /api/v1/sessions should list user sessions with paging")
    void shouldListSessionsWithPaging() throws Exception {
        // Given
        String accessToken = registerAndLoginUser("session.lister@test.local", "Password1!");
        UUID userId = getUserIdFromToken(accessToken);

        // Create sessions for this user
        createSessionForUser(userId);
        createSessionForUser(userId);

        // When / Then
        mockMvc.perform(get("/api/v1/sessions")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/sessions/public should list public sessions")
    void shouldListPublicSessions() throws Exception {
        // Given
        createPublicSessionInDatabase();

        // When / Then
        mockMvc.perform(get("/api/v1/sessions/public")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/sessions without auth should return 401")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/sessions"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.header").value("AUTH ERROR"));
    }

    @Test
    @DisplayName("GET /api/v1/sessions/{id} without auth should return 401")
    void shouldReturn401ForGetSessionWhenNotAuthenticated() throws Exception {
        // Given
        SessionEntity session = createSessionInDatabase();

        // When / Then
        mockMvc.perform(get("/api/v1/sessions/{id}", session.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/sessions/feed should return feed with filtering")
    void shouldGetFeedWithFiltering() throws Exception {
        // Given
        createPublicSessionInDatabase();

        // When / Then
        mockMvc.perform(get("/api/v1/sessions/feed")
                        .param("page", "0")
                        .param("size", "10")
                        .param("mediaType", "AUDIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/sessions/{id} should return 404 for non-existent session")
    void shouldReturn404ForNonExistentSession() throws Exception {
        // Given
        String accessToken = registerAndLoginUser("session.notfound@test.local", "Password1!");
        UUID nonExistentId = UUID.randomUUID();

        // When / Then
        mockMvc.perform(get("/api/v1/sessions/{id}", nonExistentId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    private String registerAndLoginUser(
            String email,
            String password) throws Exception {

        // Register user
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                "Test",
                "User",
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

        JsonNode loginNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return loginNode
                .path("response")
                .path("accessToken")
                .asText();
    }

    private UUID getUserIdFromToken(String accessToken) throws Exception {
        MvcResult userResult = mockMvc.perform(get("/api/v1/users/current-user")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode userNode = objectMapper.readTree(userResult.getResponse().getContentAsString());
        return UUID.fromString(userNode.path("response").path("id").asText());
    }

    private SessionEntity createSessionInDatabase() {
        SessionEntity session = SessionEntityFixtures.createDefaultSessionEntity();
        // Ensure required fields are set for database constraints
        session.setMediaUrl("https://example.com/test-session.mp3");
        // Set audit fields to avoid @PrePersist issues
        session.setCreatedBy("test@example.com");
        session.setUpdatedBy("test@example.com");
        session.setVersion((short) 0);
        return sessionRepository.save(session);
    }

    private void createPublicSessionInDatabase() {
        SessionEntity session = SessionEntityFixtures.createPublicVideoSessionEntity();
        session.setMediaUrl("https://example.com/test-public-session.mp4");
        // Set audit fields to avoid @PrePersist issues
        session.setCreatedBy("test@example.com");
        session.setUpdatedBy("test@example.com");
        session.setVersion((short) 0);
        sessionRepository.save(session);
    }

    private void createSessionForUser(UUID userId) {
        SessionEntity session = SessionEntityFixtures.createDefaultSessionEntity();
        session.setUserId(userId);
        session.setMediaUrl("https://example.com/user-session-" + UUID.randomUUID() + ".mp3");
        // Set audit fields to avoid @PrePersist issues
        session.setCreatedBy("test@example.com");
        session.setUpdatedBy("test@example.com");
        session.setVersion((short) 0);
        sessionRepository.save(session);
    }
}