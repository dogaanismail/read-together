// Domain Models - Session
export interface Session {
  id: string;
  userId: string;
  username: string;
  userProfilePicture?: string;
  title: string;
  description?: string;
  mediaUrl: string;
  mediaType: 'VIDEO' | 'AUDIO';
  durationSeconds?: number;
  fileSizeBytes?: number;
  mimeType?: string;
  processingStatus: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  processingError?: string;
  isPublic: boolean;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  readingRoomId?: string;
  transcript?: string;
  tags?: string[];
  thumbnailUrl?: string;
  bookTitle?: string;
  language?: string;
  isLive: boolean;
  authorName?: string;
  createdAt: string;
  updatedAt: string;
  formattedDuration: string;
  formattedFileSize: string;
  canEdit: boolean;
  isLiked: boolean;
}

export interface SessionCreateRequest {
  title: string;
  description?: string;
  mediaType: 'VIDEO' | 'AUDIO';
  isPublic?: boolean;
  readingRoomId?: string;
  tags?: string;
  bookTitle?: string;
  language?: string;
  isLive?: boolean;
  authorName?: string;
}

export interface SessionUpdateRequest {
  title?: string;
  description?: string;
  isPublic?: boolean;
  tags?: string;
  bookTitle?: string;
  language?: string;
}

export interface SessionFilters {
  mediaType?: string;
  search?: string;
  language?: string;
  bookTitle?: string;
}
