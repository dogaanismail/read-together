// Updated Authentication Service using secure token storage
import { api } from './api';
import { LoginRequest, RegisterRequest, TokenResponse, User, ApiResponse } from './api/models';
import { tokenStorage } from './tokenStorage';
import { FEATURE_FLAGS } from './featureFlags';

class AuthService {
  private tokenRefreshPromise: Promise<TokenResponse> | null = null;

  async login(loginRequest: LoginRequest): Promise<TokenResponse> {
    if (FEATURE_FLAGS.BYPASS_AUTH) {
      // Mock token response for UI testing
      const mockTokenResponse = {
        accessToken: 'mock-access-token',
        refreshToken: 'mock-refresh-token',
        accessTokenExpiresAt: Date.now() + 3600000, // 1 hour
        refreshTokenExpiresAt: Date.now() + 86400000, // 24 hours
      };
      this.setTokens(mockTokenResponse);
      return mockTokenResponse;
    }

    const result: ApiResponse<TokenResponse> = await api.users.login(loginRequest);

    if (result.isSuccess) {
      this.setTokens(result.response);
      this.updateApiClientToken(result.response.accessToken);
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
    if (!FEATURE_FLAGS.BYPASS_AUTH) {
      try {
        // Get refresh token value (this is conceptual since HttpOnly cookies can't be read)
        const refreshTokenRequest = { refreshToken: 'current-refresh-token' };
        await api.users.logout(refreshTokenRequest);
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

    // Ensure we have a valid token before making the request
    const token = await this.getValidAccessToken();
    if (!token) {
      throw new Error('No valid access token available');
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

    const result: ApiResponse<void> = await api.users.forgotPassword(email);
    if (!result.isSuccess) {
      throw new Error('Failed to send password reset email');
    }
  }

  // Secure token management
  private setTokens(tokenResponse: TokenResponse): void {
    tokenStorage.setAccessToken(tokenResponse.accessToken, tokenResponse.accessTokenExpiresAt);
    tokenStorage.setRefreshToken(tokenResponse.refreshToken, tokenResponse.refreshTokenExpiresAt);
  }

  async getValidAccessToken(): Promise<string | null> {
    const currentToken = tokenStorage.getAccessToken();
    
    if (currentToken) {
      return currentToken;
    }

    // Token is expired or missing, try to refresh
    if (tokenStorage.hasRefreshToken()) {
      try {
        const newTokens = await this.refreshAccessToken();
        return newTokens.accessToken;
      } catch (error) {
        console.warn('Token refresh failed:', error);
        this.removeTokens();
        return null;
      }
    }

    return null;
  }

  private async refreshAccessToken(): Promise<TokenResponse> {
    // Prevent multiple simultaneous refresh requests
    if (this.tokenRefreshPromise) {
      return this.tokenRefreshPromise;
    }

    this.tokenRefreshPromise = this.performTokenRefresh();
    
    try {
      const result = await this.tokenRefreshPromise;
      this.tokenRefreshPromise = null;
      return result;
    } catch (error) {
      this.tokenRefreshPromise = null;
      throw error;
    }
  }

  private async performTokenRefresh(): Promise<TokenResponse> {
    if (FEATURE_FLAGS.BYPASS_AUTH) {
      const mockTokenResponse = {
        accessToken: 'mock-access-token-refreshed',
        refreshToken: 'mock-refresh-token',
        accessTokenExpiresAt: Date.now() + 3600000,
        refreshTokenExpiresAt: Date.now() + 86400000,
      };
      this.setTokens(mockTokenResponse);
      return mockTokenResponse;
    }

    const result: ApiResponse<TokenResponse> = await api.users.refreshToken();
    
    if (result.isSuccess) {
      this.setTokens(result.response);
      this.updateApiClientToken(result.response.accessToken);
      return result.response;
    } else {
      throw new Error('Token refresh failed');
    }
  }

  private updateApiClientToken(token: string): void {
    api.users.setToken(token);
    // Update other domain clients as needed
    // api.readingRooms.setToken(token);
    // api.sessions.setToken(token);
  }

  removeTokens(): void {
    tokenStorage.clearAll();
    this.clearApiClientTokens();
  }

  private clearApiClientTokens(): void {
    api.users.clearToken();
    // Clear other domain clients as needed
    // api.readingRooms.clearToken();
    // api.sessions.clearToken();
  }

  isAuthenticated(): boolean {
    return tokenStorage.isAccessTokenValid() || tokenStorage.hasRefreshToken();
  }

  // Get token expiration info for UI purposes
  getTokenExpiration(): number | null {
    return tokenStorage.getAccessTokenExpiration();
  }
}

export const authService = new AuthService();

// Re-export types from the API models (no duplication)
export type { LoginRequest, RegisterRequest, TokenResponse, User, UserUpdateRequest } from './api/models';
