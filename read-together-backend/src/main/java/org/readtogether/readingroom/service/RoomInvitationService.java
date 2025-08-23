package org.readtogether.readingroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.readingroom.common.enums.InvitationStatus;
import org.readtogether.readingroom.common.enums.InvitationType;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomInvitationEntity;
import org.readtogether.readingroom.factory.InvitationResponseFactory;
import org.readtogether.readingroom.factory.ReadingRoomInvitationEntityFactory;
import org.readtogether.readingroom.model.request.InviteToRoomRequest;
import org.readtogether.readingroom.model.response.InvitationResponse;
import org.readtogether.readingroom.repository.ReadingRoomInvitationRepository;
import org.readtogether.readingroom.repository.ReadingRoomRepository;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomInvitationService {

    private final ReadingRoomInvitationRepository invitationRepository;
    private final ReadingRoomRepository roomRepository;
    private final UserService userService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public List<InvitationResponse> inviteToRoom(
            UUID roomId,
            InviteToRoomRequest request,
            UUID inviterId) {

        log.info("Creating invitations for room {} by user {}", roomId, inviterId);

        ReadingRoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));

        UserEntity inviter = userService.findUserEntityById(inviterId);

        if (!canInviteToRoom(room, inviterId)) {
            throw new RuntimeException("You don't have permission to invite people to this room");
        }

        List<ReadingRoomInvitationEntity> invitations = new ArrayList<>();
        Instant expiresAt = Instant.now().plus(request.getExpirationHours(), ChronoUnit.HOURS);

        switch (request.getInvitationType()) {
            case EMAIL -> invitations.addAll(createEmailInvitations(room, inviter, request, expiresAt));
            case DIRECT_INVITE -> invitations.addAll(createDirectInvitations(room, inviter, request, expiresAt));
            case LINK_SHARE, QR_CODE -> invitations.add(ReadingRoomInvitationEntityFactory.createShareInvitation(room, inviter, request, expiresAt));
            default -> throw new RuntimeException("Unsupported invitation type: " + request.getInvitationType());
        }

        List<ReadingRoomInvitationEntity> savedInvitations = invitationRepository.saveAll(invitations);
        return savedInvitations.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public InvitationResponse acceptInvitation(String invitationToken, UUID userId) {

        log.info("User {} accepting invitation with token {}", userId, invitationToken);

        ReadingRoomInvitationEntity invitation = invitationRepository.findByInvitationToken(invitationToken)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (invitation.isExpired()) {
            throw new RuntimeException("Invitation has expired");
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("Invitation is no longer valid");
        }

        UserEntity acceptingUser = userService.findUserEntityById(userId);

        ReadingRoomInvitationEntity updatedInvitation = ReadingRoomInvitationEntityFactory.createAcceptInvitationEntity(invitation, acceptingUser);
        ReadingRoomInvitationEntity savedInvitation = invitationRepository.save(updatedInvitation);

        return mapToResponse(savedInvitation);
    }

    @Transactional
    public InvitationResponse declineInvitation(
            String invitationToken,
            UUID userId) {

        log.info("User {} declining invitation with token {}", userId, invitationToken);

        ReadingRoomInvitationEntity invitation = invitationRepository.findByInvitationToken(invitationToken)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("Invitation is no longer valid");
        }

        ReadingRoomInvitationEntity updatedInvitation = ReadingRoomInvitationEntityFactory.createDeclineInvitationEntity(invitation);
        ReadingRoomInvitationEntity savedInvitation = invitationRepository.save(updatedInvitation);

        return mapToResponse(savedInvitation);
    }

    public List<InvitationResponse> getRoomInvitations(
            UUID roomId,
            UUID userId) {

        ReadingRoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));

        if (!room.getHost().getId().equals(userId)) {
            throw new RuntimeException("Only the host can view room invitations");
        }

        return invitationRepository.findByReadingRoomId(roomId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<InvitationResponse> getUserPendingInvitations(UUID userId) {

        UserEntity user = userService.findUserEntityById(userId);

        List<ReadingRoomInvitationEntity> invitations = new ArrayList<>();
        invitations.addAll(invitationRepository.findPendingInvitationsByUserId(userId));
        invitations.addAll(invitationRepository.findPendingInvitationsByEmail(user.getEmail()));

        return invitations.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public InvitationResponse getInvitationByToken(String invitationToken) {

        ReadingRoomInvitationEntity invitation = invitationRepository.findByInvitationToken(invitationToken)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        return mapToResponse(invitation);
    }

    public String generateShareLink(UUID roomId, UUID userId) {

        ReadingRoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));

        if (!canInviteToRoom(room, userId)) {
            throw new RuntimeException("You don't have permission to generate share link for this room");
        }

        InviteToRoomRequest request = InviteToRoomRequest.builder()
                .invitationType(InvitationType.LINK_SHARE)
                .expirationHours(168) // 7 days for share links
                .build();

        //TODO: fix here
        List<InvitationResponse> invitations = inviteToRoom(roomId, request, userId);
        return invitations.getFirst().getShareLink();
    }

    //TODO: fix here
    private boolean canInviteToRoom(
            ReadingRoomEntity room,
            UUID userId) {

        // Host can always invite
        return room.getHost().getId().equals(userId);

        // TODO: Check if user is an active participant with invite permissions
        // This would require checking room settings and participant roles
    }

    private List<ReadingRoomInvitationEntity> createEmailInvitations(
            ReadingRoomEntity room,
            UserEntity inviter,
            InviteToRoomRequest request,
            Instant expiresAt) {

        if (request.getInvitedEmails() == null || request.getInvitedEmails().isEmpty()) {
            throw new RuntimeException("Email addresses are required for email invitations");
        }

        return request.getInvitedEmails().stream()
                .map(email -> ReadingRoomInvitationEntityFactory.createEmailInvitation(
                        room, inviter, email, request.getMessage(), expiresAt))
                .toList();
    }

    private List<ReadingRoomInvitationEntity> createDirectInvitations(
            ReadingRoomEntity room,
            UserEntity inviter,
            InviteToRoomRequest request,
            Instant expiresAt) {

        if (request.getInvitedUserIds() == null || request.getInvitedUserIds().isEmpty()) {
            throw new RuntimeException("User IDs are required for direct invitations");
        }

        return request.getInvitedUserIds().stream()
                .map(userId -> {
                    UserEntity invitedUser = userService.findUserEntityById(userId);
                    return ReadingRoomInvitationEntityFactory.createDirectInvitation(
                            room, inviter, invitedUser, request.getMessage(), expiresAt);
                })
                .toList();
    }

    private InvitationResponse mapToResponse(ReadingRoomInvitationEntity invitation) {
        return InvitationResponseFactory.createResponse(invitation, baseUrl);
    }
}
