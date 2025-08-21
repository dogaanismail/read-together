package org.readtogether.readingroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.readtogether.common.utils.AuthenticationUtils;
import org.readtogether.readingroom.model.request.UpdateRoomSettingsRequest;
import org.readtogether.readingroom.model.response.RoomSettingsResponse;
import org.readtogether.readingroom.service.RoomSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms/{roomId}/settings")
@RequiredArgsConstructor
@Tag(name = "Room Settings", description = "Reading room settings and configuration endpoints")
public class RoomSettingsController {

    private final RoomSettingsService roomSettingsService;

    @PutMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Update room settings (host only)")
    public ResponseEntity<RoomSettingsResponse> updateRoomSettings(
            @Parameter(description = "Room ID") @PathVariable UUID roomId,
            @Valid @RequestBody UpdateRoomSettingsRequest request,
            Authentication authentication) {

        UUID userId = AuthenticationUtils.extractUserIdFromAuth(authentication);
        RoomSettingsResponse response = roomSettingsService.updateRoomSettings(roomId, request, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get room settings")
    public ResponseEntity<RoomSettingsResponse> getRoomSettings(
            @Parameter(description = "Room ID") @PathVariable UUID roomId) {

        RoomSettingsResponse response = roomSettingsService.getRoomSettings(roomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-password")
    @Operation(summary = "Validate room password")
    public ResponseEntity<Boolean> validateRoomPassword(
            @Parameter(description = "Room ID") @PathVariable UUID roomId,
            @RequestBody String password) {

        boolean isValid = roomSettingsService.validateRoomPassword(roomId, password);
        return ResponseEntity.ok(isValid);
    }
}
