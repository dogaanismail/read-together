// User Domain API Service
import { BaseApiClient } from '../client';
import { User, LoginRequest, RegisterRequest, TokenResponse, ApiResponse } from '../models';

export class UserApiService extends BaseApiClient {
  constructor() {
    super('users');
  }

  // Authentication methods
  async login(credentials: LoginRequest): Promise<ApiResponse<TokenResponse>> {
    return this.post<ApiResponse<TokenResponse>>('login', credentials);
  }

  async register(userData: RegisterRequest): Promise<ApiResponse<void>> {
    return this.post<ApiResponse<void>>('register', userData);
  }

  async logout(refreshToken: string): Promise<ApiResponse<void>> {
    return this.post<ApiResponse<void>>('logout', { refreshToken });
  }

  async forgotPassword(email: string): Promise<ApiResponse<void>> {
    return this.post<ApiResponse<void>>('forgot-password', { email });
  }

  // User profile methods
  async getCurrentUser(): Promise<ApiResponse<User>> {
    return this.get<ApiResponse<User>>('current-user');
  }

  async getUser(userId: string): Promise<ApiResponse<User>> {
    return this.get<ApiResponse<User>>('user', { userId });
  }

  async updateProfile(userId: string, updates: Partial<User>): Promise<ApiResponse<User>> {
    return this.put<ApiResponse<User>>(`profile/${userId}`, updates);
  }

  async uploadProfilePicture(file: File): Promise<ApiResponse<{ profilePictureUrl: string }>> {
    const formData = new FormData();
    formData.append('profilePicture', file);
    return this.post<ApiResponse<{ profilePictureUrl: string }>>('profile/picture', formData, true);
  }

  // User statistics
  async getUserStatistics(userId: string): Promise<ApiResponse<any>> {
    return this.get<ApiResponse<any>>(`${userId}/statistics`);
  }
}

export const userApi = new UserApiService();
