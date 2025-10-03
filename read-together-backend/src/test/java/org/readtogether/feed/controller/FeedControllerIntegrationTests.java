package org.readtogether.feed.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.feed.entity.FeedItemEntity;
import org.readtogether.feed.fixtures.FeedEntityFixtures;
import org.readtogether.feed.fixtures.FeedRequestFixtures;
import org.readtogether.feed.model.CreateCommentRequest;
import org.readtogether.feed.repository.FeedRepository;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@DisplayName("FeedController Integration Tests")
class FeedControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FeedRepository feedRepository;

    @Test
    @DisplayName("GET /api/v1/feed should return default feed for authenticated user")
    void shouldReturnDefaultFeedForAuthenticatedUser() throws Exception {
        // Given
        String email = "feed.test+default@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password);

        // Create some feed items with unique IDs
        FeedItemEntity feedItem1 = FeedEntityFixtures.createDefaultFeedItemEntity();
        feedItem1.setId(null); // Let database generate ID

        FeedItemEntity feedItem2 = FeedEntityFixtures.createSessionFeedItemEntity();
        feedItem2.setId(null); // Let database generate ID

        feedRepository.save(feedItem1);
        feedRepository.save(feedItem2);

        // When / Then
        mockMvc.perform(get("/api/v1/feed")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content").isArray())
                .andExpect(jsonPath("$.response.totalElements").exists())
                .andExpect(jsonPath("$.response.size").exists())
                .andExpect(jsonPath("$.response.number").exists());
    }

    @Test
    @DisplayName("GET /api/v1/feed should return empty page for new user with no feed")
    void shouldReturnEmptyPageForNewUserWithNoFeed() throws Exception {
        // Given
        String email = "feed.test+empty@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password);

        // When / Then
        mockMvc.perform(get("/api/v1/feed")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content").isArray())
                .andExpect(jsonPath("$.response.content").isEmpty())
                .andExpect(jsonPath("$.response.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/feed with type filter should return filtered results")
    void shouldReturnFilteredResultsByType() throws Exception {
        // Given
        String email = "feed.test+filter@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password);

        FeedItemEntity sessionItem = FeedEntityFixtures.createSessionFeedItemEntity();
        sessionItem.setId(null);

        FeedItemEntity achievementItem = FeedEntityFixtures.createAchievementFeedItemEntity();
        achievementItem.setId(null);

        feedRepository.save(sessionItem);
        feedRepository.save(achievementItem);

        // When / Then - filter by SESSION type
        mockMvc.perform(get("/api/v1/feed")
                        .param("type", "SESSION")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/feed/trending should return trending feed items")
    void shouldReturnTrendingFeedItems() throws Exception {
        // Given
        String email = "feed.test+trending@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password);

        FeedItemEntity trendingItem = FeedEntityFixtures.createFeedItemEntityWithCounts(
                100L,
                50L,
                20L
        );
        trendingItem.setId(null);
        feedRepository.save(trendingItem);

        // When / Then
        mockMvc.perform(get("/api/v1/feed/trending")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content").isArray())
                .andExpect(jsonPath("$.response.totalElements").exists());
    }

    @Test
    @DisplayName("GET /api/v1/feed/user/{userId} should return specific user's feed")
    void shouldReturnSpecificUserFeed() throws Exception {
        // Given
        String email = "feed.test+userFeed@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password);

        UUID targetUserId = FeedEntityFixtures.DEFAULT_USER_ID;

        FeedItemEntity userFeedItem = FeedEntityFixtures.createDefaultFeedItemEntity();
        userFeedItem.setId(null);
        feedRepository.save(userFeedItem);

        // When / Then
        mockMvc.perform(get("/api/v1/feed/user/" + targetUserId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content").isArray())
                .andExpect(jsonPath("$.response.totalElements").exists());
    }

    @Test
    @DisplayName("POST /api/v1/feed/{id}/view should increment view count")
    void shouldIncrementViewCount() throws Exception {
        // Given
        String email = "feed.test+view@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password);

        FeedItemEntity feedItem = FeedEntityFixtures.createDefaultFeedItemEntity();
        feedItem.setId(null);
        feedItem = feedRepository.save(feedItem);

        // When / Then
        mockMvc.perform(post("/api/v1/feed/" + feedItem.getId() + "/view")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/feed/{id}/like should like feed item")
    void shouldLikeFeedItem() throws Exception {
        // Given
        String email = "feed.test+like@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password);

        FeedItemEntity feedItem = FeedEntityFixtures.createDefaultFeedItemEntity();
        feedItem.setId(null);
        feedItem = feedRepository.save(feedItem);

        // When / Then
        mockMvc.perform(post("/api/v1/feed/" + feedItem.getId() + "/like")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/feed/{id}/like should unlike feed item")
    void shouldUnlikeFeedItem() throws Exception {
        // Given
        String email = "feed.test+unlike@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password);

        FeedItemEntity feedItem = FeedEntityFixtures.createDefaultFeedItemEntity();
        feedItem.setId(null);
        feedItem = feedRepository.save(feedItem);

        // First like the item
        mockMvc.perform(post("/api/v1/feed/" + feedItem.getId() + "/like")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // When / Then - Unlike the item
        mockMvc.perform(delete("/api/v1/feed/" + feedItem.getId() + "/like")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/feed/{id}/comments should return comments for feed item")
    void shouldReturnCommentsForFeedItem() throws Exception {
        // Given
        String email = "feed.test+comments@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password);

        FeedItemEntity feedItem = FeedEntityFixtures.createDefaultFeedItemEntity();
        feedItem.setId(null);
        feedItem = feedRepository.save(feedItem);

        // When / Then
        mockMvc.perform(get("/api/v1/feed/" + feedItem.getId() + "/comments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content").isArray())
                .andExpect(jsonPath("$.response.totalElements").exists());
    }

    @Test
    @DisplayName("POST /api/v1/feed/{id}/comments should create comment on feed item")
    void shouldCreateCommentOnFeedItem() throws Exception {
        // Given
        String email = "feed.test+createComment@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password);

        FeedItemEntity feedItem = FeedEntityFixtures.createDefaultFeedItemEntity();
        feedItem.setId(null);
        feedItem = feedRepository.save(feedItem);

        CreateCommentRequest commentRequest = FeedRequestFixtures.createDefaultCommentRequest();

        // When / Then
        mockMvc.perform(post("/api/v1/feed/" + feedItem.getId() + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.id").exists())
                .andExpect(jsonPath("$.response.content").value(commentRequest.getContent()))
                .andExpect(jsonPath("$.response.feedItemId").value(feedItem.getId().toString()));
    }

    @Test
    @DisplayName("POST /api/v1/feed/{id}/comments with empty content should return 400")
    void shouldReturn400WhenCommentContentIsEmpty() throws Exception {
        // Given
        String email = "feed.test+emptyComment@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password);

        FeedItemEntity feedItem = FeedEntityFixtures.createDefaultFeedItemEntity();
        feedItem.setId(null);
        feedItem = feedRepository.save(feedItem);

        CreateCommentRequest emptyCommentRequest = FeedRequestFixtures.createEmptyCommentRequest();

        // When / Then
        mockMvc.perform(post("/api/v1/feed/" + feedItem.getId() + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyCommentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/feed without auth should return 401")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/feed"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/feed/{id}/like without auth should return 401")
    void shouldReturn401WhenLikingWithoutAuth() throws Exception {
        // Given
        UUID feedItemId = UUID.randomUUID();

        // When / Then
        mockMvc.perform(post("/api/v1/feed/" + feedItemId + "/like"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/feed/{id}/comments without auth should return 401")
    void shouldReturn401WhenCommentingWithoutAuth() throws Exception {
        // Given
        UUID feedItemId = UUID.randomUUID();
        CreateCommentRequest commentRequest = FeedRequestFixtures.createDefaultCommentRequest();

        // When / Then
        mockMvc.perform(post("/api/v1/feed/" + feedItemId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isUnauthorized());
    }

    private String registerAndLogin(
            String email,
            String password) throws Exception {

        // Register user
        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                "Feed",
                "User"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        return loginAndGetAccessToken(email, password);
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
}