package org.readtogether.readingroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.readingroom.model.request.JoinRoomRequest;
import org.readtogether.readingroom.model.response.InvitationResponse;
import org.readtogether.readingroom.model.response.ReadingRoomResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomAccessService {

    private final ReadingRoomService readingRoomService;
    private final RoomInvitationService invitationService;
    private final RoomSettingsService settingsService;

    @Transactional(readOnly = true)
    public InvitationResponse getRoomFromInvitation(
            String token) {

        log.info("Getting room information from invitation token: {}", token);
        return invitationService.getInvitationByToken(token);
    }

    @Transactional
    public ReadingRoomResponse joinRoomViaInvitation(
            String token,
            JoinRoomRequest request,
            UUID userId) {

        log.info("User {} joining room via invitation token: {}", userId, token);

        InvitationResponse invitation = invitationService.getInvitationByToken(token);

        if (request != null && request.getPassword() != null) {
            validateRoomPassword(invitation.getReadingRoomId(), request.getPassword());
        }

        invitationService.acceptInvitation(token, userId);

        return readingRoomService.joinRoom(invitation.getReadingRoomId(), userId);
    }

    @Transactional
    public ReadingRoomResponse joinRoomByCode(
            JoinRoomRequest request,
            UUID userId) {

        log.info("User {} joining room by code: {}", userId, request.getRoomCode());

        ReadingRoomResponse room = readingRoomService.getRoomByCode(request.getRoomCode());

        if (request.getPassword() != null) {
            validateRoomPassword(room.getId(), request.getPassword());
        }

        return readingRoomService.joinRoom(room.getId(), userId);
    }

    private void validateRoomPassword(
            UUID roomId,
            String password) {

        boolean isValidPassword = settingsService.validateRoomPassword(roomId, password);
        if (!isValidPassword) {
            throw new RuntimeException("Invalid room password");
        }
    }
}
