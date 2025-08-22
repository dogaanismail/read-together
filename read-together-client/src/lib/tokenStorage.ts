// Secure Token Storage Implementation

interface TokenStorage {
  accessToken: string | null;
  accessTokenExpiresAt: number | null;
}

class SecureTokenStorage {
  private storage: TokenStorage = {
    accessToken: null,
    accessTokenExpiresAt: null,
  };

  // Access Token Management (in memory)
  setAccessToken(token: string, expiresAt: number): void {
    this.storage.accessToken = token;
    this.storage.accessTokenExpiresAt = expiresAt;
  }

  getAccessToken(): string | null {
    if (this.storage.accessToken && this.storage.accessTokenExpiresAt) {
      // Check if token is expired (with 5 minute buffer)
      const now = Date.now();
      const buffer = 5 * 60 * 1000; // 5 minutes
      if (now >= (this.storage.accessTokenExpiresAt - buffer)) {
        this.clearAccessToken();
        return null;
      }
    }
    return this.storage.accessToken;
  }

  clearAccessToken(): void {
    this.storage.accessToken = null;
    this.storage.accessTokenExpiresAt = null;
  }

  isAccessTokenValid(): boolean {
    return !!this.getAccessToken();
  }

  setRefreshToken(token: string, expiresAt: number): void {
    const expires = new Date(expiresAt);
    document.cookie = `refreshToken=${token}; expires=${expires.toUTCString()}; path=/; HttpOnly; Secure; SameSite=Strict`;
  }

  clearRefreshToken(): void {
    document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/; HttpOnly; Secure; SameSite=Strict';
  }

  hasRefreshToken(): boolean {
    return document.cookie.includes('refreshToken');
  }

  clearAll(): void {
    this.clearAccessToken();
    this.clearRefreshToken();
  }

  getAccessTokenExpiration(): number | null {
    return this.storage.accessTokenExpiresAt;
  }
}

export const tokenStorage = new SecureTokenStorage();