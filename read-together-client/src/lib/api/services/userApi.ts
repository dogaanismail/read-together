// User Domain API Service
import { BaseApiClient } from '../client';
import { User, LoginRequest, RegisterRequest, TokenResponse, ApiResponse, UserUpdateRequest } from '../models';

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

  async logout(refreshTokenRequest: { refreshToken: string }): Promise<ApiResponse<void>> {
    return this.post<ApiResponse<void>>('logout', refreshTokenRequest);
  }

  async forgotPassword(email: string): Promise<ApiResponse<void>> {
    // Use notification service for forgot password
    const notificationClient = new BaseApiClient('notifications');
    return notificationClient.post<ApiResponse<void>>('forgot-password', { email });
  }

  // User profile methods
  async getCurrentUser(): Promise<ApiResponse<User>> {
    return this.get<ApiResponse<User>>('current-user');
  }

  async getUser(userId: string): Promise<ApiResponse<User>> {
    return this.get<ApiResponse<User>>('user', { userId });
  }

  async updateProfile(updates: UserUpdateRequest): Promise<ApiResponse<User>> {
    return this.put<ApiResponse<User>>('profile', updates);
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

  // Token refresh
  async refreshToken(): Promise<ApiResponse<TokenResponse>> {
    return this.post<ApiResponse<TokenResponse>>('refresh-token', {});
  }
}

export const userApi = new UserApiService();
