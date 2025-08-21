// API Configuration
export const API_CONFIG = {
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  TIMEOUT: 30000,
  RETRY_ATTEMPTS: 3,
} as const;

// Common API Response Types
export interface ApiResponse<T = any> {
  httpStatus: string;
  isSuccess: boolean;
  response: T;
  time: string;
}

export interface PageResponse<T> {
  content: T[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

// Common API Error Types
export interface ApiError {
  message: string;
  status: number;
  code?: string;
  details?: any;
}

// Generic Pagination Parameters
export interface PaginationParams {
  page?: number;
  size?: number;
}

// Generic Filter Parameters
export interface FilterParams {
  search?: string;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

// Request Configuration
export interface RequestConfig extends RequestInit {
  timeout?: number;
  retries?: number;
}
