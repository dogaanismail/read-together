// Session Domain API Service
import { BaseApiClient } from '../client';
import { Session, SessionCreateRequest, SessionUpdateRequest, SessionFilters, PageResponse, PaginationParams } from '../models';

export class SessionApiService extends BaseApiClient {
  constructor() {
    super('sessions');
  }

  // Session creation methods
  async createSession(sessionData: SessionCreateRequest, file: File): Promise<Session> {
    const formData = new FormData();

    // Add session data as JSON blob
    const sessionBlob = new Blob([JSON.stringify(sessionData)], {
      type: 'application/json'
    });
    formData.append('session', sessionBlob);
    formData.append('file', file);

    return this.post<Session>('sync', formData, true);
  }

  async createSessionAsync(sessionData: SessionCreateRequest, file: File): Promise<Session> {
    const formData = new FormData();

    const sessionBlob = new Blob([JSON.stringify(sessionData)], {
      type: 'application/json'
    });
    formData.append('session', sessionBlob);
    formData.append('file', file);

    return this.post<Session>('', formData, true);
  }

  // Session retrieval methods
  async getUserSessions(params: PaginationParams = {}): Promise<PageResponse<Session>> {
    return this.getPaginated<Session>('', params);
  }

  async getPublicSessions(params: PaginationParams = {}): Promise<PageResponse<Session>> {
    return this.getPaginated<Session>('public', params);
  }

  async getSessionsFeed(params: PaginationParams & SessionFilters = {}): Promise<PageResponse<Session>> {
    return this.getPaginated<Session>('feed', params);
  }

  async getSession(sessionId: string): Promise<Session> {
    return this.get<Session>(sessionId);
  }

  async getUserSession(sessionId: string): Promise<Session> {
    return this.get<Session>(`my/${sessionId}`);
  }

  // Session management methods
  async updateSession(sessionId: string, updates: SessionUpdateRequest): Promise<Session> {
    return this.put<Session>(sessionId, updates);
  }

  async deleteSession(sessionId: string): Promise<void> {
    return this.delete<void>(sessionId);
  }

  // Session interaction methods
  async likeSession(sessionId: string): Promise<void> {
    return this.post<void>(`${sessionId}/like`);
  }

  async unlikeSession(sessionId: string): Promise<void> {
    return this.delete<void>(`${sessionId}/like`);
  }

  async incrementViewCount(sessionId: string): Promise<void> {
    try {
      return this.post<void>(`${sessionId}/view`);
    } catch (error) {
      console.warn('Failed to increment view count:', error);
    }
  }

  // Session search and filtering
  async searchSessions(query: string, params: PaginationParams = {}): Promise<PageResponse<Session>> {
    return this.getPaginated<Session>('search', { ...params, q: query });
  }

  async getSessionsByMediaType(mediaType: 'AUDIO' | 'VIDEO', params: PaginationParams = {}): Promise<PageResponse<Session>> {
    return this.getPaginated<Session>('', { ...params, mediaType });
  }
}

export const sessionApi = new SessionApiService();
