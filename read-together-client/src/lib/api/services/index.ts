// Export all domain API services with explicit imports to avoid circular dependencies
import { UserApiService } from './userApi';
import { FeedApiService } from './feedApi';
import { SessionApiService } from './sessionApi';
import { ReadingRoomApiService } from './readingRoomApi';

// Create instances
const userApi = new UserApiService();
const feedApi = new FeedApiService();
const sessionApi = new SessionApiService();
const readingRoomApi = new ReadingRoomApiService();

// Export instances
export { userApi, feedApi, sessionApi, readingRoomApi };

// Export classes
export { UserApiService, FeedApiService, SessionApiService, ReadingRoomApiService };

// Re-export the base client for custom implementations
export { BaseApiClient, createDomainClient } from '../client';

// Default API instances for immediate use
export const api = {
  users: userApi,
  feed: feedApi,
  sessions: sessionApi,
  readingRooms: readingRoomApi,
} as const;
