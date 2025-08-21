// Domain Models - Reading Room
export interface ReadingRoom {
  id: string;
  title: string;
  description?: string;
  maxParticipants: number;
  isPublic: boolean;
  roomCode: string;
  status: 'WAITING' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  scheduledStartTime?: string;
  actualStartTime?: string;
  endTime?: string;
  hostId: string;
  hostName: string;
  participantCount: number;
  participants: RoomParticipant[];
  settings?: RoomSettings;
  createdAt: string;
  updatedAt: string;
}

export interface RoomParticipant {
  id: string;
  userId: string;
  username: string;
  profilePictureUrl?: string;
  role: 'HOST' | 'PARTICIPANT';
  joinedAt: string;
  isActive: boolean;
}

export interface RoomSettings {
  allowChat: boolean;
  allowRecording: boolean;
  maxDuration?: number;
  requireApproval: boolean;
  allowSpectators: boolean;
}

export interface CreateRoomRequest {
  title: string;
  description?: string;
  maxParticipants?: number;
  isPublic?: boolean;
  scheduledStartTime?: string;
  settings?: Partial<RoomSettings>;
}

export interface RoomInvitation {
  id: string;
  roomId: string;
  roomTitle: string;
  inviterId: string;
  inviterName: string;
  inviteeEmail: string;
  status: 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'EXPIRED';
  createdAt: string;
  expiresAt: string;
}
