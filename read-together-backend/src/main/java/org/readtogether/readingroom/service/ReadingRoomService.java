package org.readtogether.readingroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.readtogether.readingroom.entity.ReadingRoomEntity;
import org.readtogether.readingroom.entity.ReadingRoomParticipantEntity;
import org.readtogether.readingroom.entity.ReadingRoomSettingsEntity;
import org.readtogether.readingroom.factory.ReadingRoomEntityFactory;
import org.readtogether.readingroom.factory.ReadingRoomParticipantEntityFactory;
import org.readtogether.readingroom.factory.ReadingRoomResponseFactory;
import org.readtogether.readingroom.model.request.CreateReadingRoomRequest;
import org.readtogether.readingroom.model.response.ReadingRoomResponse;
import org.readtogether.readingroom.repository.ReadingRoomParticipantRepository;
import org.readtogether.readingroom.repository.ReadingRoomRepository;
import org.readtogether.user.entity.UserEntity;
import org.readtogether.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.readtogether.readingroom.common.enums.ParticipantStatus.JOINED;
import static org.readtogether.readingroom.common.enums.RoomStatus.ACTIVE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadingRoomService {

    private final ReadingRoomRepository readingRoomRepository;
    private final ReadingRoomParticipantRepository participantRepository;
    private final UserService userService;
    private final RoomSettingsService roomSettingsService;

    @Transactional
    public ReadingRoomResponse createRoom(
            CreateReadingRoomRequest request,
            UUID hostId) {

        log.info("Creating reading room for host: {}", hostId);

        UserEntity host = userService.findUserEntityById(hostId);

        ReadingRoomEntity room = ReadingRoomEntityFactory.createReadingRoomEntity(request, host);
        ReadingRoomEntity savedRoom = readingRoomRepository.save(room);

        roomSettingsService.createDefaultSettings(savedRoom);

        joinRoom(savedRoom.getId(), hostId);

        return createRoomResponse(savedRoom);
    }

    @Transactional
    public ReadingRoomResponse joinRoom(
            UUID roomId,
            UUID userId) {

        log.info("User {} joining room {}", userId, roomId);

        ReadingRoomEntity room = findRoomById(roomId);
        UserEntity user = userService.findUserEntityById(userId);

        participantRepository.findByReadingRoomIdAndUserId(roomId, userId)
                .ifPresent(existing -> {
                    if (existing.getStatus() == JOINED) {
                        throw new RuntimeException("User is already in the room");
                    }
                });

        Integer currentParticipants = participantRepository.countActiveParticipantsByRoomId(roomId);
        if (currentParticipants >= room.getMaxParticipants()) {
            throw new RuntimeException("Room is full");
        }

        ReadingRoomSettingsEntity settings = roomSettingsService.getSettingsEntity(roomId);

        ReadingRoomParticipantEntity participant = ReadingRoomParticipantEntityFactory
                .createParticipant(room, user, settings.isAutoMuteNewJoiners());

        participantRepository.save(participant);

        return createRoomResponse(room);
    }

    @Transactional
    public void leaveRoom(
            UUID roomId,
            UUID userId) {

        log.info("User {} leaving room {}", userId, roomId);

        ReadingRoomParticipantEntity participant = participantRepository
                .findByReadingRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("User is not in this room"));

        ReadingRoomParticipantEntity updatedParticipant = ReadingRoomParticipantEntityFactory.participantLeft(participant);
        participantRepository.save(updatedParticipant);
    }

    @Transactional
    public ReadingRoomResponse startRoom(
            UUID roomId,
            UUID hostId) {

        log.info("Starting room {} by host {}", roomId, hostId);

        ReadingRoomEntity room = findRoomById(roomId);

        if (!room.getHost().getId().equals(hostId)) {
            throw new RuntimeException("Only the host can start the room");
        }

        room.setStatus(ACTIVE);
        room.setActualStartTime(Instant.now());

        ReadingRoomEntity savedRoom = readingRoomRepository.save(room);
        return createRoomResponse(savedRoom);
    }

    public List<ReadingRoomResponse> getPublicWaitingRooms() {

        return readingRoomRepository.findPublicWaitingRooms()
                .stream()
                .map(this::createRoomResponse)
                .toList();
    }

    public List<ReadingRoomResponse> getUserHostedRooms(UUID userId) {

        return readingRoomRepository.findByHostId(userId)
                .stream()
                .map(this::createRoomResponse)
                .toList();
    }

    public ReadingRoomResponse getRoomByCode(String roomCode) {

        ReadingRoomEntity room = readingRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found with code: " + roomCode));
        return createRoomResponse(room);
    }

    private ReadingRoomEntity findRoomById(UUID roomId) {

        return readingRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
    }

    private ReadingRoomResponse createRoomResponse(ReadingRoomEntity room) {

        Integer currentParticipants = participantRepository.countActiveParticipantsByRoomId(room.getId());
        return ReadingRoomResponseFactory.createResponse(room, currentParticipants);
    }
}
