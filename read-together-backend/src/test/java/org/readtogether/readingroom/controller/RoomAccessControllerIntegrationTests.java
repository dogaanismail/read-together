package org.readtogether.readingroom.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.readingroom.common.enums.InvitationType;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.readingroom.model.request.InviteToRoomRequest;
import org.readtogether.readingroom.model.request.JoinRoomRequest;
import org.readtogether.readingroom.model.request.UpdateRoomSettingsRequest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("RoomAccessController Integration Tests")
class RoomAccessControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/room/join should return room information from invitation token")
    void shouldGetRoomFromInvitation() throws Exception {
        // Given: user creates a room and generates a share link
        String hostEmail = "host_invitation@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        // Create room first
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomId = roomNode.path("id").asText();

        // Generate share link
        MvcResult shareResult = mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations/share-link")
                        .header("Authorization", "Bearer " + hostToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode shareNode = objectMapper.readTree(shareResult.getResponse().getContentAsString());
        String shareToken = shareNode.path("invitationToken").asText();

        // When: get room from invitation (public access - no auth required)
        mockMvc.perform(get("/api/v1/room/join")
                        .param("token", shareToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readingRoomId").value(roomId))
                .andExpect(jsonPath("$.invitationType").value("LINK_SHARE"))
                .andExpect(jsonPath("$.invitationToken").value(shareToken));
    }

    @Test
    @DisplayName("POST /api/v1/room/join should join room via invitation token")
    void shouldJoinRoomViaInvitation() throws Exception {
        // Given: host creates room and invites user
        String hostEmail = "host_user_2@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        String inviteeEmail = "invitee@test.local";
        String inviteePassword = "Password1!";
        String inviteeToken = registerAndLogin(inviteeEmail, inviteePassword, "Invitee", "User");

        // Create room first
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomId = roomNode.path("id").asText();

        // Create invitation
        InviteToRoomRequest inviteRequest = ReadingRoomRequestFixtures.createInviteToRoomRequest(
                InvitationType.EMAIL,
                List.of(inviteeEmail),
                null,
                "Please join our reading room!",
                24
        );

        MvcResult inviteResult = mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode inviteNode = objectMapper.readTree(inviteResult.getResponse().getContentAsString());
        String invitationToken = inviteNode.path(0).path("invitationToken").asText();

        // When: join a room via invitation
        mockMvc.perform(post("/api/v1/room/join")
                        .param("token", invitationToken)
                        .header("Authorization", "Bearer " + inviteeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId))
                .andExpect(jsonPath("$.currentParticipants").value(2)); // host + invitee
    }

    @Test
    @DisplayName("POST /api/v1/room/join-by-code should join room by room code")
    void shouldJoinRoomByCode() throws Exception {
        // Given: user creates a public room
        String hostEmail = "host_user@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        String joinerEmail = "joiner_2@test.local";
        String joinerPassword = "Password1!";
        String joinerToken = registerAndLogin(joinerEmail, joinerPassword, "Joiner", "User");

        // Create a public room
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createDefaultCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomCode = roomNode.path("roomCode").asText();

        // When: join the room by code
        JoinRoomRequest joinRequest = ReadingRoomRequestFixtures
                .createJoinRoomRequest(roomCode, null);

        mockMvc.perform(post("/api/v1/room/join-by-code")
                        .header("Authorization", "Bearer " + joinerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomCode").value(roomCode))
                .andExpect(jsonPath("$.currentParticipants").value(2)); // host + joiner
    }

    @Test
    @DisplayName("POST /api/v1/room/join-by-code should handle private room with password")
    void shouldJoinPrivateRoomWithPassword() throws Exception {
        // Given: user creates private room with password
        String hostEmail = "host2@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        String joinerEmail = "joiner@test.local";
        String joinerPassword = "Password1!";
        String joinerToken = registerAndLogin(joinerEmail, joinerPassword, "Joiner", "User");

        // Create a private room
        CreateReadingRoomRequest createRequest = ReadingRoomRequestFixtures.createPrivateCreateReadingRoomRequest();
        MvcResult roomResult = mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode roomNode = objectMapper.readTree(roomResult.getResponse().getContentAsString());
        String roomId = roomNode.path("id").asText();
        String roomCode = roomNode.path("roomCode").asText();

        // Set room password
        UpdateRoomSettingsRequest settingsRequest =
                ReadingRoomRequestFixtures.createPrivateUpdateRoomSettingsRequest();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/rooms/" + roomId + "/settings")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settingsRequest)))
                .andExpect(status().isOk());

        // When: join the room by code with the correct password
        JoinRoomRequest joinRequest = ReadingRoomRequestFixtures.createJoinRoomRequest(
                roomCode,
                "newPassword123"
        );

        mockMvc.perform(post("/api/v1/room/join-by-code")
                        .header("Authorization", "Bearer " + joinerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomCode").value(roomCode))
                .andExpect(jsonPath("$.currentParticipants").value(2)); // host + joiner
    }

    @Test
    @DisplayName("POST /api/v1/room/join should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When / Then
        mockMvc.perform(post("/api/v1/room/join")
                        .param("token", "some-token"))
                .andExpect(status().isUnauthorized());
    }

    private String registerAndLogin(
            String email,
            String password,
            String firstName,
            String lastName) throws Exception {

        RegisterRequest register = RequestFixtures.createRegisterRequest(
                email,
                password,
                firstName,
                lastName,
                "user"
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