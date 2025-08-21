// Domain Models - User
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
  lastActivityDate?: string;
}

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
