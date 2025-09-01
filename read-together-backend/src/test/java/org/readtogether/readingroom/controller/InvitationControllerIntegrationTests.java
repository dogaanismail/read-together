package org.readtogether.readingroom.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.readtogether.common.BaseIntegrationTest;
import org.readtogether.readingroom.common.enums.InvitationType;
import org.readtogether.readingroom.fixtures.ReadingRoomRequestFixtures;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.readingroom.model.request.InviteToRoomRequest;
import org.readtogether.user.fixtures.RequestFixtures;
import org.readtogether.user.model.request.LoginRequest;
import org.readtogether.user.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@DisplayName("InvitationController Integration Tests")
class InvitationControllerIntegrationTests extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/invitations/my-invitations should return user's pending invitations")
    void shouldGetMyPendingInvitations() throws Exception {
        // Given: host creates room
        String hostEmail = "host@test.local";
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

        // Given: invitee user
        String inviteeEmail = "invitee@test.local";
        String inviteePassword = "Password1!";
        String inviteeToken = registerAndLogin(inviteeEmail, inviteePassword, "Invitee", "User");

        // Create invitation for the invitee
        InviteToRoomRequest inviteRequest = ReadingRoomRequestFixtures.createInviteToRoomRequest(
                InvitationType.EMAIL,
                List.of(inviteeEmail),
                null,
                "Join our reading room!",
                24
        );

        mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isCreated());

        // When: invitee checks their pending invitations
        mockMvc.perform(get("/api/v1/invitations/my-invitations")
                        .header("Authorization", "Bearer " + inviteeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].readingRoomId").value(roomId))
                .andExpect(jsonPath("$[0].invitationType").value("EMAIL"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].message").value("Join our reading room!"));
    }

    @Test
    @DisplayName("GET /api/v1/invitations/{token} should return invitation details")
    void shouldGetInvitationByToken() throws Exception {
        // Given: user creates room and invites someone
        String hostEmail = "hosttest.local@gmail.com";
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

        // Create invitation
        InviteToRoomRequest inviteRequest = ReadingRoomRequestFixtures.createDefaultEmailInviteRequest();
        MvcResult inviteResult = mockMvc.perform(post("/api/v1/rooms/" + roomId + "/invitations")
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode inviteNode = objectMapper.readTree(inviteResult.getResponse().getContentAsString());
        String invitationToken = inviteNode.path(0).path("invitationToken").asText();

        // When: get invitation by token
        mockMvc.perform(get("/api/v1/invitations/" + invitationToken)
                        .header("Authorization", "Bearer " + hostToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invitationToken").value(invitationToken))
                .andExpect(jsonPath("$.readingRoomId").value(roomId))
                .andExpect(jsonPath("$.invitationType").value("EMAIL"));
    }

    @Test
    @DisplayName("POST /api/v1/invitations/{token}/accept should accept invitation")
    void shouldAcceptInvitation() throws Exception {
        // Given: user creates room and invites someone
        String hostEmail = "host2@test.local";
        String hostPassword = "Password1!";
        String hostToken = registerAndLogin(hostEmail, hostPassword, "Host", "User");

        String inviteeEmail = "invitee2@test.local";
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

        // Create an invitation with the invitee's email
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

        // When: accept invitation
        mockMvc.perform(post("/api/v1/invitations/" + invitationToken + "/accept")
                        .header("Authorization", "Bearer " + inviteeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.acceptedAt").exists());
    }

    @Test
    @DisplayName("GET /api/v1/invitations/my-invitations should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/v1/invitations/my-invitations"))
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