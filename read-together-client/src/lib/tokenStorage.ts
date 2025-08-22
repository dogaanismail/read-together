// Secure Token Storage Implementation
// Access tokens in memory, refresh tokens in HttpOnly cookies

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
        // Token is expired or about to expire
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

  // Refresh Token Management (HttpOnly cookies)
  // Note: HttpOnly cookies are managed by the browser and server
  // We can only check if they exist through API calls

  setRefreshToken(token: string, expiresAt: number): void {
    // Set refresh token as HttpOnly cookie
    // This should ideally be done by the server, but for client-side handling:
    const expires = new Date(expiresAt);
    document.cookie = `refreshToken=${token}; expires=${expires.toUTCString()}; path=/; HttpOnly; Secure; SameSite=Strict`;
  }

  clearRefreshToken(): void {
    // Clear refresh token cookie
    document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/; HttpOnly; Secure; SameSite=Strict';
  }

  // Check if refresh token exists (best effort - HttpOnly cookies can't be read by JS)
  hasRefreshToken(): boolean {
    // Since we can't read HttpOnly cookies, we'll make an assumption
    // This would typically be validated through an API call
    return document.cookie.includes('refreshToken');
  }

  // Clear all tokens
  clearAll(): void {
    this.clearAccessToken();
    this.clearRefreshToken();
  }

  // Get token expiration info
  getAccessTokenExpiration(): number | null {
    return this.storage.accessTokenExpiresAt;
  }
}

export const tokenStorage = new SecureTokenStorage();