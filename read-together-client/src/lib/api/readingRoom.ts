import { apiClient } from './client';
import { ReadingRoomRequest, ReadingRoomResponse, InviteToRoomRequest, InvitationResponse } from './types';

// Reading Room API Service - matches your Spring Boot endpoints
export class ReadingRoomApi {

  // GET /api/v1/rooms/public
  static async getPublicRooms(): Promise<ReadingRoomResponse[]> {
    return apiClient.get<ReadingRoomResponse[]>('/rooms/public');
  }

  // GET /api/v1/rooms/my-rooms
  static async getMyHostedRooms(): Promise<ReadingRoomResponse[]> {
    return apiClient.get<ReadingRoomResponse[]>('/rooms/my-rooms');
  }

  // POST /api/v1/rooms
  static async createRoom(request: ReadingRoomRequest): Promise<ReadingRoomResponse> {
    return apiClient.post<ReadingRoomResponse>('/rooms', request);
  }

  // POST /api/v1/rooms/{roomId}/join
  static async joinRoom(roomId: string): Promise<ReadingRoomResponse> {
    return apiClient.post<ReadingRoomResponse>(`/rooms/${roomId}/join`);
  }

  // POST /api/v1/rooms/{roomId}/leave
  static async leaveRoom(roomId: string): Promise<void> {
    return apiClient.post<void>(`/rooms/${roomId}/leave`);
  }

  // POST /api/v1/rooms/{roomId}/start
  static async startRoom(roomId: string): Promise<ReadingRoomResponse> {
    return apiClient.post<ReadingRoomResponse>(`/rooms/${roomId}/start`);
  }

  // GET /api/v1/rooms/code/{roomCode}
  static async getRoomByCode(roomCode: string): Promise<ReadingRoomResponse> {
    return apiClient.get<ReadingRoomResponse>(`/rooms/code/${roomCode}`);
  }

  // POST /api/v1/rooms/{roomId}/invitations
  static async inviteToRoom(roomId: string, request: InviteToRoomRequest): Promise<InvitationResponse[]> {
    return apiClient.post<InvitationResponse[]>(`/rooms/${roomId}/invitations`, request);
  }

  // GET /api/v1/rooms/{roomId}/invitations
  static async getRoomInvitations(roomId: string): Promise<InvitationResponse[]> {
    return apiClient.get<InvitationResponse[]>(`/rooms/${roomId}/invitations`);
  }

  // POST /api/v1/rooms/{roomId}/invitations/share-link
  static async generateShareLink(roomId: string): Promise<{ shareLink: string }> {
    return apiClient.post<{ shareLink: string }>(`/rooms/${roomId}/invitations/share-link`);
  }

  // GET /api/v1/invitations/my-invitations
  static async getMyPendingInvitations(): Promise<InvitationResponse[]> {
    return apiClient.get<InvitationResponse[]>('/invitations/my-invitations');
  }

  // POST /api/v1/invitations/{token}/accept
  static async acceptInvitation(token: string): Promise<InvitationResponse> {
    return apiClient.post<InvitationResponse>(`/invitations/${token}/accept`);
  }

  // POST /api/v1/invitations/{token}/decline
  static async declineInvitation(token: string): Promise<InvitationResponse> {
    return apiClient.post<InvitationResponse>(`/invitations/${token}/decline`);
  }

  // POST /api/v1/room/join-by-code
  static async joinRoomByCode(roomCode: string, password?: string): Promise<ReadingRoomResponse> {
    return apiClient.post<ReadingRoomResponse>('/room/join-by-code', { roomCode, password });
  }
}
