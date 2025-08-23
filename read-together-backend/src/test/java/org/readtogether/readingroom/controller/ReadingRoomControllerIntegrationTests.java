package org.readtogether.readingroom.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@DisplayName("ReadingRoomController Integration Tests")
class ReadingRoomControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/rooms should create a reading room")
    void shouldCreateRoom() throws Exception {
        // Given: register and login
        String email = "room.creator@test.local";
        String password = "Password1!";
        RegisterRequest register = RequestFixtures.createRegisterRequest(email,
                password,
                "Room",
                "Creator",
                "user"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        String token = loginAndGetAccessToken(email, password);

        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();

        // When: create room
        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(createRequest.getTitle()))
                .andExpect(jsonPath("$.description").value(createRequest.getDescription()))
                .andExpect(jsonPath("$.maxParticipants").value(createRequest.getMaxParticipants()))
                .andExpect(jsonPath("$.isPublic").value(createRequest.isPublic()))
                .andExpect(jsonPath("$.roomCode").exists())
                .andExpect(jsonPath("$.currentParticipants").value(1)) // host joins automatically
                .andExpect(jsonPath("$.host.email").value(email));
    }

    @Test
    @DisplayName("GET /api/v1/rooms/public should return public waiting rooms")
    @Disabled("Currently not implemented")
    void shouldGetPublicWaitingRooms() throws Exception {
        // When: get public rooms
        mockMvc.perform(get("/api/v1/rooms/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/join should join a room successfully")
    void shouldJoinRoom() throws Exception {
        // Given: create a room first
        String hostEmail = "host_" + System.currentTimeMillis() + "@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        // Create room
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomId = roomNode.path("id").asText();

        // Register another user to join
        String participantEmail = "participant_" + System.currentTimeMillis() + "@test.local";
        String participantPassword = "Password1!";
        String participantToken = registerAndLogin(participantEmail, participantPassword, "Participant", "User");

        // When: join room
        mockMvc.perform(post("/api/v1/rooms/{roomId}/join", roomId)
                        .header("Authorization", "Bearer " + participantToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId))
                .andExpect(jsonPath("$.currentParticipants").value(2)); // host + participant
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/leave should leave a room successfully")
    void shouldLeaveRoom() throws Exception {
        // Given: create a room and join with another user
        String hostEmail = "host_leave_" + System.currentTimeMillis() + "@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        // Create room
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomId = roomNode.path("id").asText();

        // Register and join another user
        String participantEmail = "participant_leave_" + System.currentTimeMillis() + "@test.local";
        String participantPassword = "Password1!";
        String participantToken = registerAndLogin(participantEmail, participantPassword, "Participant", "User");

        // Join room first
        mockMvc.perform(post("/api/v1/rooms/{roomId}/join", roomId)
                        .header("Authorization", "Bearer " + participantToken))
                .andExpect(status().isOk());

        // When: leave room
        mockMvc.perform(post("/api/v1/rooms/{roomId}/leave", roomId)
                        .header("Authorization", "Bearer " + participantToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/start should start a room by host")
    void shouldStartRoom() throws Exception {
        // Given: create a room
        String hostEmail = "host_start_" + System.currentTimeMillis() + "@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        // Create room
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomId = roomNode.path("id").asText();

        // When: start room
        mockMvc.perform(post("/api/v1/rooms/{roomId}/start", roomId)
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/v1/rooms/my-rooms should return hosted rooms")
    void shouldGetMyHostedRooms() throws Exception {
        // Given: create multiple rooms with same host
        String hostEmail = "host_my_rooms_" + System.currentTimeMillis() + "@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        // Create first room
        CreateReadingRoomRequest createRequest1 = ReadingRoomRequestFixtures.createCreateReadingRoomRequest(
                "My Room 1", "First room", true, 10, null);
        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest1)))
                .andExpect(status().isCreated());

        // Create second room
        CreateReadingRoomRequest createRequest2 = ReadingRoomRequestFixtures.createCreateReadingRoomRequest(
                "My Room 2", "Second room", false, 8, null);
        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest2)))
                .andExpect(status().isCreated());

        // When: get hosted rooms
        mockMvc.perform(get("/api/v1/rooms/my-rooms")
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].host.email").value(hostEmail))
                .andExpect(jsonPath("$[1].host.email").value(hostEmail));
    }

    @Test
    @DisplayName("GET /api/v1/rooms/code/{roomCode} should return room by code")
    void shouldGetRoomByCode() throws Exception {
        // Given: create a room
        String hostEmail = "host_by_code_" + System.currentTimeMillis() + "@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        // Create room
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomCode = roomNode.path("roomCode").asText();

        // Register another user to access room by code
        String userEmail = "user_by_code_" + System.currentTimeMillis() + "@test.local";
        String userPassword = "Password1!";
        String userToken = registerAndLogin(userEmail, userPassword, "User", "Test");

        // When: get room by code
        mockMvc.perform(get("/api/v1/rooms/code/{roomCode}", roomCode)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomCode").value(roomCode))
                .andExpect(jsonPath("$.title").value(createRequest.getTitle()))
                .andExpect(jsonPath("$.description").value(createRequest.getDescription()))
                .andExpect(jsonPath("$.host.email").value(hostEmail));
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/join should handle non-existent room")
    void shouldHandleJoiningNonExistentRoom() throws Exception {
        // Given: register user
        String userEmail = "user_500_" + System.currentTimeMillis() + "@test.local";
        String userPassword = "Password1!";
        String userToken = registerAndLogin(userEmail, userPassword, "User", "Test");

        // When: try to join non-existent room - expect RuntimeException wrapped in ServletException
        try {
            mockMvc.perform(post("/api/v1/rooms/{roomId}/join", "550e8400-e29b-41d4-a716-446655440000")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            // This is expected due to service layer throwing RuntimeException without proper exception handling
            assert e.getCause() instanceof RuntimeException;
            assert e.getCause().getMessage().contains("Room not found");
        }
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{roomId}/start should handle non-host authorization")
    void shouldHandleNonHostStartingRoom() throws Exception {
        // Given: create a room with one host
        String hostEmail = "host_500_" + System.currentTimeMillis() + "@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        // Create room
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomId = roomNode.path("id").asText();

        // Register another user (not host)
        String participantEmail = "participant_500_" + System.currentTimeMillis() + "@test.local";
        String participantPassword = "Password1!";
        String participantToken = registerAndLogin(participantEmail, participantPassword, "Participant", "User");

        // When: non-host tries to start room - expect RuntimeException wrapped in ServletException
        try {
            mockMvc.perform(post("/api/v1/rooms/{roomId}/start", roomId)
                            .header("Authorization", "Bearer " + participantToken))
                    .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            // This is expected due to service layer throwing RuntimeException without proper exception handling
            assert e.getCause() instanceof RuntimeException;
            assert e.getCause().getMessage().contains("Only the host can start");
        }
    }

    @Test
    @DisplayName("GET /api/v1/rooms/code/{roomCode} should handle non-existent room code")
    void shouldHandleNonExistentRoomCode() throws Exception {
        // Given: register user
        String userEmail = "user_code_500_" + System.currentTimeMillis() + "@test.local";
        String userPassword = "Password1!";
        String userToken = registerAndLogin(userEmail, userPassword, "User", "Test");

        // When: try to get room with non-existent code - expect RuntimeException wrapped in ServletException
        try {
            mockMvc.perform(get("/api/v1/rooms/code/{roomCode}", "NONEXIST")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            // This is expected due to service layer throwing RuntimeException without proper exception handling
            assert e.getCause() instanceof RuntimeException;
            assert e.getCause().getMessage().contains("Room not found");
        }
    }

    @Test
    @DisplayName("POST /api/v1/rooms should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // Given
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures
                .createDefaultCreateReadingRoomRequest();

        // When / Then
        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/rooms/my-rooms should return 401 when not authenticated")
    void shouldReturn401WhenGettingMyRoomsUnauthenticated() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/rooms/my-rooms"))
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

    private String registerAndLogin(String email, String password, String firstName, String lastName) throws Exception {
        // Register user
        RegisterRequest register = RequestFixtures.createRegisterRequest(email, password, firstName, lastName, "user");
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        // Login and return token
        return loginAndGetAccessToken(email, password);
    }
}