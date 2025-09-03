package org.readtogether.chat.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.chat.model.request.ChatRoomCreateRequest;
import org.readtogether.chat.fixtures.ChatRequestFixtures;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.readtogether.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@DisplayName("ChatController Integration Tests")
class ChatControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("GET /api/v1/chat/rooms should return user chat rooms with authentication")
    void shouldReturnUserChatRoomsWithAuth() throws Exception {
        // Given: register and login user
        String email = "chat.user" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Chat", "User");

        // When & Then
        mockMvc.perform(get("/api/v1/chat/rooms")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists());
    }

    @Test
    @DisplayName("POST /api/v1/chat/rooms should create chat room with authentication")
    void shouldCreateChatRoomWithAuth() throws Exception {
        // Given: register user and login
        String email = "chat.creator" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Chat", "Creator");

        ChatRoomCreateRequest request = ChatRequestFixtures.createDefaultChatRoomCreateRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/chat/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.name").value("Test Room"));
    }

    @Test
    @DisplayName("GET /api/v1/chat/rooms/{roomId}/messages should return messages for participant")
    void shouldReturnMessagesForParticipant() throws Exception {
        // Given: register user, login, and create room
        String email = "chat.reader" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Chat", "Reader");

        ChatRoomCreateRequest roomRequest = ChatRequestFixtures.createDefaultChatRoomCreateRequest();
        MvcResult createResult = mockMvc.perform(post("/api/v1/chat/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String roomId = createNode.path("response").path("id").asText();

        // When & Then
        mockMvc.perform(get("/api/v1/chat/rooms/{roomId}/messages", roomId)
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/chat/rooms/{roomId}/read should mark messages as read")
    void shouldMarkMessagesAsRead() throws Exception {
        // Given: register user, login, and create room
        String email = "chat.marker" + System.currentTimeMillis() + "@test.local";
        String password = "Password1!";
        String token = registerAndLogin(email, password, "Chat", "Marker");

        ChatRoomCreateRequest roomRequest = ChatRequestFixtures.createDefaultChatRoomCreateRequest();
        MvcResult createResult = mockMvc.perform(post("/api/v1/chat/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String roomId = createNode.path("response").path("id").asText();

        // When & Then
        mockMvc.perform(post("/api/v1/chat/rooms/{roomId}/read", roomId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/chat/direct/{otherUserId} should create or get direct chat")
    void shouldCreateOrGetDirectChat() throws Exception {
        // Given: register two users
        long timestamp = System.currentTimeMillis();
        String email1 = "chat.user1." + timestamp + "@test.local";
        String password1 = "Password1!";
        String token1 = registerAndLogin(email1, password1, "Chat", "User1");

        String email2 = "chat.user2." + timestamp + "@test.local";
        String password2 = "Password1!";
        registerAndLogin(email2, password2, "Chat", "User2");

        // Get user2 ID
        UUID user2Id = getUserIdByEmail(email2);

        // When & Then
        mockMvc.perform(get("/api/v1/chat/direct/{otherUserId}", user2Id)
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.id").exists())
                .andExpect(jsonPath("$.response.name").value("Direct chat between Chat User1 and Chat User2"));


    }

    @Test
    @DisplayName("GET /api/v1/chat/rooms without authentication should return 401")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/chat/rooms"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/chat/rooms without authentication should return 401")
    void shouldReturn401WhenCreatingRoomWithoutAuth() throws Exception {
        ChatRoomCreateRequest request = ChatRequestFixtures.createDefaultChatRoomCreateRequest();

        mockMvc.perform(post("/api/v1/chat/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/chat/rooms/{roomId}/messages without access should return 403")
    void shouldReturn403WhenAccessingRoomWithoutParticipation() throws Exception {
        // Given: register two users
        long timestamp = System.currentTimeMillis();
        String email1 = "chat.creator." + timestamp + "@test.local";
        String password1 = "Password1!";
        String token1 = registerAndLogin(email1, password1, "Chat", "Creator");

        String email2 = "chat.outsider." + timestamp + "@test.local";
        String password2 = "Password1!";
        String token2 = registerAndLogin(email2, password2, "Chat", "Outsider");

        // User1 creates a room
        ChatRoomCreateRequest roomRequest = ChatRequestFixtures.createDefaultChatRoomCreateRequest();
        MvcResult createResult = mockMvc.perform(post("/api/v1/chat/rooms")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String roomId = createNode.path("response").path("id").asText();

        // User2 tries to access the room
        mockMvc.perform(get("/api/v1/chat/rooms/{roomId}/messages", roomId)
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isForbidden());
    }

    private String registerAndLogin(
            String email,
            String password,
            String firstName,
            String lastName) throws Exception {

        RegisterRequest registerRequest = RequestFixtures.createRegisterRequest(
                email,
                password,
                firstName,
                lastName
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Login and get token
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

    private UUID getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntity::getId)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}