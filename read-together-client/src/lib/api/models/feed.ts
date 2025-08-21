// Domain Models - Feed
export interface FeedItem {
  id: string;
  userId: string;
  username: string;
  userProfilePicture?: string;
  itemType: 'SESSION' | 'ACHIEVEMENT' | 'MILESTONE' | 'ROOM_JOIN' | 'STREAK';
  referenceId: string;
  title: string;
  description?: string;
  mediaUrl?: string;
  thumbnailUrl?: string;
  isPublic: boolean;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  metadata?: string;
  createdAt: string;
  timeAgo: string;
  isLiked: boolean;
  formattedEngagement: string;
}

export interface FeedFilters {
  type?: string;
  search?: string;
  language?: string;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}
