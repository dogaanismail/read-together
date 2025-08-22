import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { authService, User } from '../lib/auth';
import { FEATURE_FLAGS } from '../lib/featureFlags';

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string, firstName: string, lastName: string, role: string) => Promise<void>;
  logout: () => Promise<void>;
  isAuthenticated: boolean;
  refreshUser: () => Promise<void>;
  updateUser: (userData: Partial<User>) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const refreshUser = async () => {
    try {
      // If auth is bypassed, use mock data
      if (FEATURE_FLAGS.BYPASS_AUTH) {
        setUser(FEATURE_FLAGS.MOCK_USER_DATA as User);
        return;
      }

      if (authService.isAuthenticated()) {
        const currentUser = await authService.getCurrentUser();
        setUser(currentUser);
      }
    } catch (error) {
      console.error('Failed to fetch user:', error);
      if (!FEATURE_FLAGS.BYPASS_AUTH) {
        authService.removeTokens();
        setUser(null);
      }
    }
  };

  useEffect(() => {
    const initAuth = async () => {
      await refreshUser();
      setLoading(false);
    };

    initAuth();

    // Set up automatic token refresh check
    const tokenCheckInterval = setInterval(async () => {
      if (authService.isAuthenticated()) {
        try {
          // Try to get a valid token (this will trigger refresh if needed)
          const token = await authService.getValidAccessToken();
          if (!token) {
            // Token refresh failed, user needs to re-authenticate
            setUser(null);
          }
        } catch (error) {
          console.error('Token validation failed:', error);
          setUser(null);
        }
      }
    }, 5 * 60 * 1000); // Check every 5 minutes

    return () => {
      clearInterval(tokenCheckInterval);
    };
  }, []);

  const login = async (email: string, password: string) => {
    // If auth is bypassed, just set mock user data
    if (FEATURE_FLAGS.BYPASS_AUTH) {
      setUser(FEATURE_FLAGS.MOCK_USER_DATA as User);
      return;
    }

    setLoading(true);
    try {
      await authService.login({ email, password });
      await refreshUser();
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const register = async (email: string, password: string, firstName: string, lastName: string, role: string) => {
    // If auth is bypassed, just set mock user data
    if (FEATURE_FLAGS.BYPASS_AUTH) {
      setUser(FEATURE_FLAGS.MOCK_USER_DATA as User);
      return;
    }

    setLoading(true);
    try {
      await authService.register({ email, password, firstName, lastName, role });
    } catch (error) {
      console.error('Registration failed:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    // If auth is bypassed, just clear the mock user
    if (FEATURE_FLAGS.BYPASS_AUTH) {
      setUser(null);
      return;
    }

    setLoading(true);
    try {
      await authService.logout();
      setUser(null);
    } catch (error) {
      console.error('Logout failed:', error);
      // Clear user state even if the logout request fails
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const updateUser = (userData: Partial<User>) => {
    if (user) {
      setUser({ ...user, ...userData });
    }
  };

  const value: AuthContextType = {
    user,
    loading,
    login,
    register,
    logout,
    isAuthenticated: !!user,
    refreshUser,
    updateUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
