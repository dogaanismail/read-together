package org.readtogether.readingroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.AuthenticationUtils;
import org.readtogether.readingroom.model.request.InviteToRoomRequest;
import org.readtogether.readingroom.model.response.InvitationResponse;
import org.readtogether.readingroom.service.RoomInvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms/{roomId}/invitations")
@RequiredArgsConstructor
@Tag(name = "Room Invitations", description = "Reading room invitation management endpoints")
public class RoomInvitationController {

    private final RoomInvitationService invitationService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Invite people to a reading room")
    public ResponseEntity<List<InvitationResponse>> inviteToRoom(
            @Parameter(description = "Room ID") @PathVariable UUID roomId,
            @Valid @RequestBody InviteToRoomRequest request,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        List<InvitationResponse> invitations = invitationService.inviteToRoom(roomId, request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(invitations);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Get all invitations for a room (host only)")
    public ResponseEntity<List<InvitationResponse>> getRoomInvitations(
            @Parameter(description = "Room ID") @PathVariable UUID roomId,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        List<InvitationResponse> invitations = invitationService.getRoomInvitations(roomId, userId);

        return ResponseEntity.ok(invitations);
    }

    @PostMapping("/share-link")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Generate a share link for the room")
    public ResponseEntity<Map<String, String>> generateShareLink(
            @Parameter(description = "Room ID") @PathVariable UUID roomId,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        String shareLink = invitationService.generateShareLink(roomId, userId);

        return ResponseEntity.ok(Map.of("shareLink", shareLink));
    }
}
