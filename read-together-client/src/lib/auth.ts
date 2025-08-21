// Authentication API service
const API_BASE_URL = 'http://localhost:8080/api/v1';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresAt: number;
  refreshTokenExpiresAt: number;
}

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  username?: string;
  profilePictureUrl?: string;
  bio?: string;
  readingStreak: number;
  totalSessions: number;
  totalReadingTimeSeconds: number;
  longestStreak: number;
  totalActiveDays: number;
}

export interface CustomResponse<T> {
  httpStatus: string;
  isSuccess: boolean;
  response: T;
  time: string;
}

class AuthService {
  private getAuthHeaders() {
    const token = this.getToken();
    return {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    };
  }

  async login(loginRequest: LoginRequest): Promise<TokenResponse> {
    const response = await fetch(`${API_BASE_URL}/users/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(loginRequest),
    });

    if (!response.ok) {
      throw new Error('Login failed');
    }

    const result: CustomResponse<TokenResponse> = await response.json();

    if (result.isSuccess) {
      this.setToken(result.response.accessToken);
      this.setRefreshToken(result.response.refreshToken);
      return result.response;
    } else {
      throw new Error('Login failed');
    }
  }

  async register(registerRequest: RegisterRequest): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/users/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(registerRequest),
    });

    if (!response.ok) {
      throw new Error('Registration failed');
    }

    const result: CustomResponse<void> = await response.json();

    if (!result.isSuccess) {
      throw new Error('Registration failed');
    }
  }

  async logout(): Promise<void> {
    const refreshToken = this.getRefreshToken();

    if (refreshToken) {
      try {
        await fetch(`${API_BASE_URL}/users/logout`, {
          method: 'POST',
          headers: this.getAuthHeaders(),
          body: JSON.stringify({ refreshToken }),
        });
      } catch (error) {
        console.warn('Logout request failed, but clearing local storage anyway');
      }
    }

    this.removeTokens();
  }

  async getCurrentUser(): Promise<User> {
    const response = await fetch(`${API_BASE_URL}/users/current-user`, {
      method: 'GET',
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error('Failed to get current user');
    }

    const result: CustomResponse<User> = await response.json();

    if (result.isSuccess) {
      return result.response;
    } else {
      throw new Error('Failed to get current user');
    }
  }

  setToken(token: string): void {
    localStorage.setItem('accessToken', token);
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
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}

export const authService = new AuthService();
