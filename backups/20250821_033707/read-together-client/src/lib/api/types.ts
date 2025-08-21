// Reading Room API Types - matching your Spring Boot backend
export interface ReadingRoomRequest {
  title: string;
  description?: string;
  maxParticipants?: number;
  isPublic?: boolean;
  scheduledStartTime?: string; // ISO string
}

export interface ReadingRoomResponse {
  id: string;
  title: string;
  description?: string;
  maxParticipants: number;
  currentParticipants: number;
  isPublic: boolean;
  roomCode: string;
  status: 'WAITING' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  scheduledStartTime?: string;
  actualStartTime?: string;
  endTime?: string;
  host: {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
  };
  createdAt: string;
}

export interface InviteToRoomRequest {
  invitationType: 'EMAIL' | 'DIRECT_INVITE' | 'LINK_SHARE' | 'QR_CODE' | 'CONTACT_SHARE';
  invitedEmails?: string[];
  invitedUserIds?: string[];
  message?: string;
  expirationHours?: number;
}

export interface InvitationResponse {
  id: string;
  readingRoomId: string;
  roomTitle: string;
  roomCode: string;
  invitationType: string;
  status: 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'EXPIRED' | 'CANCELLED';
  shareLink: string;
  qrCodeUrl: string;
  message?: string;
  expiresAt: string;
  isExpired: boolean;
}
