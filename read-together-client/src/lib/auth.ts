// Updated Authentication Service using the new generic API architecture
import { api } from './api';
import { LoginRequest, RegisterRequest, TokenResponse, User, ApiResponse } from './api/models';
import { FEATURE_FLAGS } from './featureFlags';

class AuthService {
  async login(loginRequest: LoginRequest): Promise<TokenResponse> {
    if (FEATURE_FLAGS.BYPASS_AUTH) {
      // Mock token response for UI testing
      return {
        accessToken: 'mock-access-token',
        refreshToken: 'mock-refresh-token',
        accessTokenExpiresAt: Date.now() + 3600000, // 1 hour
        refreshTokenExpiresAt: Date.now() + 86400000, // 24 hours
      };
    }

    const result: ApiResponse<TokenResponse> = await api.users.login(loginRequest);

    if (result.isSuccess) {
      this.setToken(result.response.accessToken);
      this.setRefreshToken(result.response.refreshToken);
      return result.response;
    } else {
      throw new Error('Login failed');
    }
  }

  async register(registerRequest: RegisterRequest): Promise<void> {
    if (FEATURE_FLAGS.BYPASS_AUTH) {
      console.log('Mock: User registered', registerRequest.email);
      return;
    }

    const result: ApiResponse<void> = await api.users.register(registerRequest);

    if (!result.isSuccess) {
      throw new Error('Registration failed');
    }
  }

  async logout(): Promise<void> {
    const refreshToken = this.getRefreshToken();

    if (!FEATURE_FLAGS.BYPASS_AUTH && refreshToken) {
      try {
        await api.users.logout(refreshToken);
      } catch (error) {
        console.warn('Logout request failed, but clearing local storage anyway');
      }
    }

    this.removeTokens();
  }

  async getCurrentUser(): Promise<User> {
    if (FEATURE_FLAGS.BYPASS_AUTH) {
      return FEATURE_FLAGS.MOCK_USER_DATA as User;
    }

    const result: ApiResponse<User> = await api.users.getCurrentUser();

    if (result.isSuccess) {
      return result.response;
    } else {
      throw new Error('Failed to get current user');
    }
  }

  async forgotPassword(email: string): Promise<void> {
    if (FEATURE_FLAGS.BYPASS_AUTH) {
      console.log('Mock: Password reset email sent to', email);
      return;
    }

    await api.users.forgotPassword(email);
  }

  setToken(token: string): void {
    localStorage.setItem('accessToken', token);
    // Update API client token
    api.users.setToken(token);
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  setRefreshToken(token: string): void {
    localStorage.setItem('refreshToken', token);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  removeTokens(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    // Clear API client token
    api.users.clearToken();
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}

export const authService = new AuthService();

// Re-export types from the API models (no duplication)
export type { LoginRequest, RegisterRequest, TokenResponse, User } from './api/models';
