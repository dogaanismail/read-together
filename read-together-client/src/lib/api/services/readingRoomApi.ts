// Reading Room Domain API Service
import { BaseApiClient } from '../client';
import { ReadingRoom, CreateRoomRequest, RoomInvitation, PageResponse, PaginationParams } from '../models';

export class ReadingRoomApiService extends BaseApiClient {
  constructor() {
    super('rooms');
  }

  // Room management methods
  async createRoom(roomData: CreateRoomRequest): Promise<ReadingRoom> {
    return this.post<ReadingRoom>('', roomData);
  }

  async getRoom(roomId: string): Promise<ReadingRoom> {
    return this.get<ReadingRoom>(roomId);
  }

  async updateRoom(roomId: string, updates: Partial<CreateRoomRequest>): Promise<ReadingRoom> {
    return this.put<ReadingRoom>(roomId, updates);
  }

  async deleteRoom(roomId: string): Promise<void> {
    return this.delete<void>(roomId);
  }

  // Room participation methods
  async joinRoom(roomId: string): Promise<ReadingRoom> {
    return this.post<ReadingRoom>(`${roomId}/join`);
  }

  async leaveRoom(roomId: string): Promise<void> {
    return this.post<void>(`${roomId}/leave`);
  }

  async startRoom(roomId: string): Promise<ReadingRoom> {
    return this.post<ReadingRoom>(`${roomId}/start`);
  }

  async endRoom(roomId: string): Promise<ReadingRoom> {
    return this.post<ReadingRoom>(`${roomId}/end`);
  }

  // Room discovery methods
  async getPublicRooms(params: PaginationParams = {}): Promise<PageResponse<ReadingRoom>> {
    return this.getPaginated<ReadingRoom>('public', params);
  }

  async getMyHostedRooms(params: PaginationParams = {}): Promise<PageResponse<ReadingRoom>> {
    return this.getPaginated<ReadingRoom>('my-rooms', params);
  }

  async getJoinedRooms(params: PaginationParams = {}): Promise<PageResponse<ReadingRoom>> {
    return this.getPaginated<ReadingRoom>('joined', params);
  }

  // Room invitations
  async inviteToRoom(roomId: string, emails: string[]): Promise<RoomInvitation[]> {
    return this.post<RoomInvitation[]>(`${roomId}/invite`, { emails });
  }

  async getInvitations(params: PaginationParams = {}): Promise<PageResponse<RoomInvitation>> {
    return this.getPaginated<RoomInvitation>('invitations', params);
  }

  async respondToInvitation(invitationId: string, response: 'ACCEPT' | 'DECLINE'): Promise<void> {
    return this.post<void>(`invitations/${invitationId}/respond`, { response });
  }

  // Room settings
  async updateRoomSettings(roomId: string, settings: any): Promise<ReadingRoom> {
    return this.put<ReadingRoom>(`${roomId}/settings`, settings);
  }

  async getRoomParticipants(roomId: string): Promise<any[]> {
    return this.get<any[]>(`${roomId}/participants`);
  }

  async removeParticipant(roomId: string, participantId: string): Promise<void> {
    return this.delete<void>(`${roomId}/participants/${participantId}`);
  }
}

export const readingRoomApi = new ReadingRoomApiService();
