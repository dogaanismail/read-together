package org.readtogether.readingroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.AuthenticationUtils;
import org.readtogether.readingroom.model.response.InvitationResponse;
import org.readtogether.readingroom.service.RoomInvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
@Tag(name = "Invitations", description = "General invitation management endpoints")
public class InvitationController {

    private final RoomInvitationService invitationService;

    @GetMapping("/my-invitations")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Get current user's pending invitations")
    public ResponseEntity<List<InvitationResponse>> getMyPendingInvitations(Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        List<InvitationResponse> invitations = invitationService.getUserPendingInvitations(userId);
        return ResponseEntity.ok(invitations);
    }

    @GetMapping("/{invitationToken}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Get invitation details by token")
    public ResponseEntity<InvitationResponse> getInvitationByToken(
            @Parameter(description = "Invitation token") @PathVariable String invitationToken) {

        InvitationResponse invitation = invitationService.getInvitationByToken(invitationToken);
        return ResponseEntity.ok(invitation);
    }

    @PostMapping("/{invitationToken}/accept")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Accept an invitation")
    public ResponseEntity<InvitationResponse> acceptInvitation(
            @Parameter(description = "Invitation token") @PathVariable String invitationToken,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        InvitationResponse invitation = invitationService.acceptInvitation(invitationToken, userId);
        return ResponseEntity.ok(invitation);
    }

    @PostMapping("/{invitationToken}/decline")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Decline an invitation")
    public ResponseEntity<InvitationResponse> declineInvitation(
            @Parameter(description = "Invitation token") @PathVariable String invitationToken,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        InvitationResponse invitation = invitationService.declineInvitation(invitationToken, userId);
        return ResponseEntity.ok(invitation);
    }
}
