package org.readtogether.library.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.library.fixtures.LibraryRequestFixtures;
import org.readtogether.library.model.request.BookCreateRequest;
import org.readtogether.library.model.request.BookProgressUpdateRequest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@DisplayName("BookProgressController Integration Tests")
class BookProgressControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("PUT /api/v1/library/progress/{bookId} should update reading progress")
    void shouldUpdateReadingProgress() throws Exception {
        // Given: register and login user
        String email = "progress.updater@test.local";
        String password = "Password1!";

        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                "Progress",
                "Updater"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);
        String bookId = createBookAndGetId(token);

        BookProgressUpdateRequest progressUpdate = LibraryRequestFixtures.createDefaultUpdateProgressRequest();

        // When: update progress
        mockMvc.perform(put("/api/v1/library/progress/" + bookId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(progressUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(progressUpdate.getStatus().toString()))
                .andExpect(jsonPath("$.currentPage").value(progressUpdate.getCurrentPage()))
                .andExpect(jsonPath("$.progressPercentage").value(progressUpdate.getProgressPercentage()))
                .andExpect(jsonPath("$.favorite").value(progressUpdate.getIsFavorite()));
    }

    @Test
    @DisplayName("GET /api/v1/library/progress/favorites should return favorite books")
    void shouldReturnFavoriteBooks() throws Exception {
        // Given: register and login user
        String email = "favorites.viewer@test.local";
        String password = "Password1!";

        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                "Favorites",
                "Viewer"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);
        String bookId = createBookAndGetId(token);

        // Mark book as favorite
        BookProgressUpdateRequest favoriteUpdate = LibraryRequestFixtures.createToggleFavoriteRequest(
                java.util.UUID.fromString(bookId), true
        );

        mockMvc.perform(put("/api/v1/library/progress/" + bookId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(favoriteUpdate)))
                .andExpect(status().isOk());

        // When: get favorite books
        mockMvc.perform(get("/api/v1/library/progress/favorites")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/library/progress/recent should return recently read books")
    void shouldReturnRecentlyReadBooks() throws Exception {
        // Given: register and login user
        String email = "recent.reader@test.local";
        String password = "Password1!";

        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                "Recent",
                "Reader"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);
        String bookId = createBookAndGetId(token);

        // Update progress to simulate recent reading
        BookProgressUpdateRequest progressUpdate = LibraryRequestFixtures.createStartReadingRequest();

        mockMvc.perform(put("/api/v1/library/progress/" + bookId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(progressUpdate)))
                .andExpect(status().isOk());

        // When: get recent books
        mockMvc.perform(get("/api/v1/library/progress/recent")
                        .param("days", "30")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/library/progress/stats/reading-time should return total reading time")
    void shouldReturnTotalReadingTime() throws Exception {
        // Given: register and login user
        String email = "reading.time.tracker@test.local";
        String password = "Password1!";

        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                "Reading",
                "TimeTracker"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // When: get total reading time
        mockMvc.perform(get("/api/v1/library/progress/stats/reading-time")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("0")); // Should be 0 initially
    }

    @Test
    @DisplayName("GET /api/v1/library/progress/stats/completed-count should return completed books count")
    void shouldReturnCompletedBooksCount() throws Exception {
        // Given: register and login user
        String email = "completion.tracker@test.local";
        String password = "Password1!";

        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                "Completion",
                "Tracker"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        // When: get completed books count
        mockMvc.perform(get("/api/v1/library/progress/stats/completed-count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("0")); // Should be 0 initially
    }

    @Test
    @DisplayName("DELETE /api/v1/library/progress/{bookId} should delete progress")
    void shouldDeleteProgress() throws Exception {
        // Given: register and login user
        String email = "progress.deleter@test.local";
        String password = "Password1!";

        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                "Progress",
                "Deleter"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);
        String bookId = createBookAndGetId(token);

        // Create some progress first
        BookProgressUpdateRequest progressUpdate = LibraryRequestFixtures.createDefaultUpdateProgressRequest();

        mockMvc.perform(put("/api/v1/library/progress/" + bookId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(progressUpdate)))
                .andExpect(status().isOk());

        // When: delete progress
        mockMvc.perform(delete("/api/v1/library/progress/" + bookId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        String bookId = "123e4567-e89b-12d3-a456-426614174001";
        BookProgressUpdateRequest progressUpdate = LibraryRequestFixtures.createDefaultUpdateProgressRequest();

        // When: try to update progress without authentication
        mockMvc.perform(put("/api/v1/library/progress/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(progressUpdate)))
                .andExpect(status().isUnauthorized());

        // When: try to get favorites without authentication
        mockMvc.perform(get("/api/v1/library/progress/favorites"))
                .andExpect(status().isUnauthorized());
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

}