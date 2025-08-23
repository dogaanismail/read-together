package org.readtogether.notification.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.notification.fixtures.NotificationRequestFixtures;
import org.readtogether.notification.controller.NotificationPreferencesController.PushSubscriptionRequest;
import org.readtogether.notification.model.NotificationPreferencesUpdateRequest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/notifications should return notifications with authentication")
    void shouldGetNotificationsWithAuthentication() throws Exception {
        // Given: register and login
        String accessToken = registerAndLoginUser("notification.user@test.local", "Password1!");

        // When / Then
        mockMvc.perform(get("/api/v1/notifications")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/notifications without auth should return 401")
    void shouldReturn401ForGetNotificationsWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/notifications/unread-count should return count with authentication")
    void shouldGetUnreadCountWithAuthentication() throws Exception {
        // Given: register and login
        String accessToken = registerAndLoginUser("notification.count@test.local", "Password1!");

        // When / Then
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").isNumber());
    }

    @Test
    @DisplayName("GET /api/v1/notifications/unread-count without auth should return 401")
    void shouldReturn401ForUnreadCountWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/unread-count"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/v1/notifications/{id}/read should return 404 for non-existent notification")
    void shouldReturn404ForMarkAsReadNonExistentNotification() throws Exception {
        // Given: register and login
        String accessToken = registerAndLoginUser("notification.mark@test.local", "Password1!");

        // When / Then
        mockMvc.perform(put("/api/v1/notifications/550e8400-e29b-41d4-a716-446655440999/read")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/notifications/{id}/read without auth should return 401")
    void shouldReturn401ForMarkAsReadWhenNotAuthenticated() throws Exception {
        mockMvc.perform(put("/api/v1/notifications/550e8400-e29b-41d4-a716-446655440999/read"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/v1/notifications/mark-all-read should mark all notifications as read")
    void shouldMarkAllAsRead() throws Exception {
        // Given: register and login
        String accessToken = registerAndLoginUser("notification.markall@test.local", "Password1!");

        // When / Then
        mockMvc.perform(put("/api/v1/notifications/mark-all-read")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.markedAsRead").isNumber());
    }

    @Test
    @DisplayName("PUT /api/v1/notifications/mark-all-read without auth should return 401")
    void shouldReturn401ForMarkAllReadWhenNotAuthenticated() throws Exception {
        mockMvc.perform(put("/api/v1/notifications/mark-all-read"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/notification-preferences should return preferences with authentication")
    void shouldGetPreferencesWithAuthentication() throws Exception {
        // Given: register and login
        String accessToken = registerAndLoginUser("notification.prefs@test.local", "Password1!");

        // When / Then
        mockMvc.perform(get("/api/v1/notification-preferences")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailNotifications").isBoolean())
                .andExpect(jsonPath("$.pushNotifications").isBoolean())
                .andExpect(jsonPath("$.sessionLikes").isBoolean())
                .andExpect(jsonPath("$.newFollowers").isBoolean())
                .andExpect(jsonPath("$.liveStreamAlerts").isBoolean())
                .andExpect(jsonPath("$.weeklyDigest").isBoolean())
                .andExpect(jsonPath("$.marketingEmails").isBoolean())
                .andExpect(jsonPath("$.uploadStatus").isBoolean());
    }

    @Test
    @DisplayName("GET /api/v1/notification-preferences without auth should return 401")
    void shouldReturn401ForGetPreferencesWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/notification-preferences"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/v1/notification-preferences should update preferences")
    void shouldUpdatePreferences() throws Exception {
        // Given: register and login
        String accessToken = registerAndLoginUser("notification.update@test.local", "Password1!");
        NotificationPreferencesUpdateRequest updateRequest = NotificationRequestFixtures.createDefaultPreferencesUpdateRequest();

        // When / Then
        mockMvc.perform(put("/api/v1/notification-preferences")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailNotifications").value(updateRequest.getEmailNotifications()))
                .andExpect(jsonPath("$.pushNotifications").value(updateRequest.getPushNotifications()))
                .andExpect(jsonPath("$.sessionLikes").value(updateRequest.getSessionLikes()));
    }

    @Test
    @DisplayName("PUT /api/v1/notification-preferences without auth should return 401")
    void shouldReturn401ForUpdatePreferencesWhenNotAuthenticated() throws Exception {
        NotificationPreferencesUpdateRequest updateRequest = NotificationRequestFixtures.createDefaultPreferencesUpdateRequest();

        mockMvc.perform(put("/api/v1/notification-preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/notification-preferences/push-subscription should update subscription")
    void shouldUpdatePushSubscription() throws Exception {
        // Given: register and login
        String accessToken = registerAndLoginUser("notification.push@test.local", "Password1!");
        PushSubscriptionRequest pushRequest = NotificationRequestFixtures.createDefaultPushSubscriptionRequest();

        // When / Then
        mockMvc.perform(post("/api/v1/notification-preferences/push-subscription")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pushRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/notification-preferences/push-subscription without auth should return 401")
    void shouldReturn401ForPushSubscriptionWhenNotAuthenticated() throws Exception {
        PushSubscriptionRequest pushRequest = NotificationRequestFixtures.createDefaultPushSubscriptionRequest();

        mockMvc.perform(post("/api/v1/notification-preferences/push-subscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pushRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should handle notification pagination parameters")
    void shouldHandleNotificationPaginationParameters() throws Exception {
        // Given: register and login
        String accessToken = registerAndLoginUser("notification.pagination@test.local", "Password1!");

        // When / Then - Test custom pagination
        mockMvc.perform(get("/api/v1/notifications")
                        .param("page", "1")
                        .param("size", "5")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(1))
                .andExpect(jsonPath("$.pageable.pageSize").value(5));
    }

    private String registerAndLoginUser
            (String email,
             String password) throws Exception {
        
        // Register user
        RegisterRequest registerRequest = RequestFixtures.createRegisterRequest(email, password, "Test", "User", "user");

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Login and get an access token
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