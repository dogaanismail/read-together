package org.readtogether.library.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.library.fixtures.LibraryRequestFixtures;
import org.readtogether.library.model.request.BookCreateRequest;
import org.readtogether.library.service.BookSessionService;
import org.readtogether.session.fixtures.SessionRequestFixtures;
import org.readtogether.session.model.request.SessionCreateRequest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@DisplayName("BookSessionController Integration Tests")
class BookSessionControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookSessionService bookSessionService;

    @Test
    @DisplayName("GET /api/v1/library/sessions/user should return user's reading sessions")
    void shouldReturnUserReadingSessions() throws Exception {
        // Given: register and login user
        String email = "session.reader@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "Session", "Reader", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // When: get user sessions
        mockMvc.perform(get("/api/v1/library/sessions/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/library/sessions/user/book/{bookId} should return sessions for specific book")
    void shouldReturnSessionsForSpecificBook() throws Exception {
        // Given: register and login user
        String email = "book.session.reader@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "BookSession", "Reader", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);
        String bookId = createBookAndGetId(token);

        // When: get sessions for the specific book
        mockMvc.perform(get("/api/v1/library/sessions/user/book/" + bookId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/library/sessions/user/recent should return recent reading sessions")
    void shouldReturnRecentReadingSessions() throws Exception {
        // Given: register and login user
        String email = "recent.session.reader@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "RecentSession", "Reader", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        UUID sessionId = UUID.fromString(createSessionAndGetId(token));
        UUID bookId = UUID.fromString(createBookAndGetId(token));

        bookSessionService.createBookSession(sessionId, bookId, userId, 0, 10 );

        // When: get recent sessions
        mockMvc.perform(get("/api/v1/library/sessions/user/recent")
                        .param("days", "30")
                        .header("User-ID", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].sessionId").value(sessionId.toString()))
                .andExpect(jsonPath("$[0].bookId").value(bookId.toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$[0].pagesRead").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/library/sessions/stats/reading-time/{bookId} should return total reading time for book")
    void shouldReturnTotalReadingTimeForBook() throws Exception {
        // Given: register and login user
        String email = "reading.time.stats@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "ReadingTime", "Stats", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);
        String bookId = createBookAndGetId(token);

        // When: get total reading time for a book
        mockMvc.perform(get("/api/v1/library/sessions/stats/reading-time/" + bookId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("0")); // Should be 0 initially
    }

    @Test
    @DisplayName("GET /api/v1/library/sessions/stats/pages-read/{bookId} should return total pages read for book")
    void shouldReturnTotalPagesReadForBook() throws Exception {
        // Given: register and login user
        String email = "pages.read.stats@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "PagesRead", "Stats", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);
        String bookId = createBookAndGetId(token);

        // When: get total pages read for a book
        mockMvc.perform(get("/api/v1/library/sessions/stats/pages-read/" + bookId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("0")); // Should be 0 initially
    }

    @Test
    @DisplayName("GET /api/v1/library/sessions/stats/session-count/{bookId} should return session count for book")
    void shouldReturnSessionCountForBook() throws Exception {
        // Given: register and login user
        String email = "session.count.stats@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "SessionCount", "Stats", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);
        String bookId = createBookAndGetId(token);

        // When: get session count for a book
        mockMvc.perform(get("/api/v1/library/sessions/stats/session-count/" + bookId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("0")); // Should be 0 initially
    }

    @Test
    @DisplayName("DELETE /api/v1/library/sessions/{sessionId} should delete book session")
    void shouldDeleteBookSession() throws Exception {
        // Given: register and login user
        String email = "session.deleter@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "Session", "Deleter", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // For this test, we would need to create a session first
        // Since we don't have a POST endpoint for creating sessions in the controller,
        // let's test it with a mock session ID
        String sessionId = UUID.randomUUID().toString();

        // When: delete session (this might return 404 if the session doesn't exist)
        mockMvc.perform(delete("/api/v1/library/sessions/" + sessionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/library/sessions/{sessionId}/exists should check if session exists")
    void shouldCheckIfSessionExists() throws Exception {
        // Given: register and login user
        String email = "session.checker@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "Session", "Checker", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // Test with a non-existent session ID
        String sessionId = UUID.randomUUID().toString();

        // When: check if a session exists
        mockMvc.perform(get("/api/v1/library/sessions/" + sessionId + "/exists")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("false")); // Should be false for non-existent session
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        String bookId = "123e4567-e89b-12d3-a456-426614174001";

        // When: try to get user sessions without authentication
        mockMvc.perform(get("/api/v1/library/sessions/user"))
                .andExpect(status().isUnauthorized());

        // When: try to get book sessions without authentication
        mockMvc.perform(get("/api/v1/library/sessions/user/book/" + bookId))
                .andExpect(status().isUnauthorized());

        // When: try to get reading time stats without authentication
        mockMvc.perform(get("/api/v1/library/sessions/stats/reading-time/" + bookId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/library/sessions/stats/average-progress should return average progress")
    void shouldReturnAverageProgress() throws Exception {
        // Given: register and login user
        String email = "average.progress@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email, password, "Average", "Progress", "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // When: get average progress (this endpoint might be in BookProgressController)
        // Note: this test assumes the endpoint exists in this controller
        mockMvc.perform(get("/api/v1/library/progress/stats/average-progress")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0")); // Should be 0.0 initially
    }

    private String loginAndGetAccessToken(
            String email,
            String password) throws Exception {

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

    private String createBookAndGetId(String token) throws Exception {
        BookCreateRequest createRequest = LibraryRequestFixtures.createDefaultAddBookRequest();

        MvcResult createResult = mockMvc.perform(post("/api/v1/library/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        JsonNode createJsonNode = objectMapper.readTree(createResponse);
        return createJsonNode.get("id").asText();
    }

    private String createSessionAndGetId(String token) throws Exception {

        SessionCreateRequest request = SessionRequestFixtures.createPublicVideoCreateRequest();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "fake video content".getBytes()
        );
        MockMultipartFile sessionPart = new MockMultipartFile(
                "session",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MvcResult createResult = mockMvc.perform(multipart("/api/v1/sessions/sync")
                        .file(sessionPart)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.mediaType").value(request.getMediaType().toString()))
                .andExpect(jsonPath("$.public").value(request.isPublic()))
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        JsonNode createJsonNode = objectMapper.readTree(createResponse);
        return createJsonNode.get("id").asText();
    }

}