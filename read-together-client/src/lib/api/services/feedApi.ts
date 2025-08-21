// Feed Domain API Service
import { BaseApiClient } from '../client';
import { FeedItem, FeedFilters, PageResponse, PaginationParams } from '../models';

export class FeedApiService extends BaseApiClient {
  constructor() {
    super('feed');
  }

  // Feed retrieval methods
  async getFeed(params: PaginationParams & FeedFilters = {}): Promise<PageResponse<FeedItem>> {
    return this.getPaginated<FeedItem>('', params);
  }

  async getTrendingFeed(params: PaginationParams = {}): Promise<PageResponse<FeedItem>> {
    return this.getPaginated<FeedItem>('trending', params);
  }

  async getUserFeed(userId: string, params: PaginationParams = {}): Promise<PageResponse<FeedItem>> {
    return this.getPaginated<FeedItem>(`user/${userId}`, params);
  }

  // Feed interaction methods
  async likeFeedItem(feedItemId: string): Promise<void> {
    return this.post<void>(`${feedItemId}/like`);
  }

  async unlikeFeedItem(feedItemId: string): Promise<void> {
    return this.delete<void>(`${feedItemId}/like`);
  }

  async incrementViewCount(feedItemId: string): Promise<void> {
    try {
      return this.post<void>(`${feedItemId}/view`);
    } catch (error) {
      // Silently fail view count increment
      console.warn('Failed to increment view count:', error);
    }
  }

  async shareFeedItem(feedItemId: string, platform: string): Promise<void> {
    return this.post<void>(`${feedItemId}/share`, { platform });
  }
}

export const feedApi = new FeedApiService();
