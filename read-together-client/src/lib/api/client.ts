// Enhanced API Base Configuration with Generic Domain Support
import { API_CONFIG, PageResponse, ApiError, RequestConfig } from './config';

// Generic HTTP Client with authentication and domain support
export class BaseApiClient {
  protected baseURL: string;
  protected domain: string;
  private token: string | null = null;

  constructor(domain: string, baseURL: string = API_CONFIG.BASE_URL) {
    this.baseURL = baseURL;
    this.domain = domain;
    this.token = this.getStoredToken();
  }

  private getStoredToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  public setToken(token: string): void {
    this.token = token;
    localStorage.setItem('accessToken', token);
  }

  public clearToken(): void {
    this.token = null;
    localStorage.removeItem('accessToken');
  }

  protected getAuthHeaders(): Record<string, string> {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };

    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    return headers;
  }

  protected buildUrl(endpoint: string): string {
    const cleanEndpoint = endpoint.startsWith('/') ? endpoint.slice(1) : endpoint;
    return `${this.baseURL}/${this.domain}/${cleanEndpoint}`;
  }

  protected buildQueryParams(params: Record<string, any>): string {
    const searchParams = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        searchParams.append(key, String(value));
      }
    });

    const queryString = searchParams.toString();
    return queryString ? `?${queryString}` : '';
  }

  protected async request<T>(
    endpoint: string,
    config: RequestConfig = {}
  ): Promise<T> {
    const url = this.buildUrl(endpoint);
    const timeout = config.timeout || API_CONFIG.TIMEOUT;
    const retries = config.retries || API_CONFIG.RETRY_ATTEMPTS;

    const requestConfig: RequestInit = {
      headers: {
        ...this.getAuthHeaders(),
        ...config.headers,
      },
      ...config,
    };

    // Remove custom properties from requestConfig
    delete (requestConfig as any).timeout;
    delete (requestConfig as any).retries;

    return this.executeWithRetry(url, requestConfig, retries, timeout);
  }

  private async executeWithRetry<T>(
    url: string,
    config: RequestInit,
    retries: number,
    timeout: number
  ): Promise<T> {
    for (let attempt = 0; attempt <= retries; attempt++) {
      try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), timeout);

        const response = await fetch(url, {
          ...config,
          signal: controller.signal,
        });

        clearTimeout(timeoutId);

        if (!response.ok) {
          const error: ApiError = {
            message: `API Error: ${response.status} ${response.statusText}`,
            status: response.status,
          };

          // Try to parse error response
          try {
            const errorData = await response.json();
            error.details = errorData;
            if (errorData.message) error.message = errorData.message;
          } catch {
            // Ignore parsing errors
          }

          throw error;
        }

        // Handle different response types
        const contentType = response.headers.get('content-type');
        if (contentType?.includes('application/json')) {
          return response.json();
        } else if (config.method === 'DELETE' || response.status === 204) {
          return undefined as T;
        } else {
          return response.text() as T;
        }
      } catch (error) {
        if (attempt === retries) {
          throw error;
        }
        // Wait before retry (exponential backoff)
        await new Promise(resolve => setTimeout(resolve, Math.pow(2, attempt) * 1000));
      }
    }
    throw new Error('Max retries exceeded');
  }

  // Generic CRUD Methods
  public async get<T>(endpoint: string, params?: Record<string, any>): Promise<T> {
    const queryParams = params ? this.buildQueryParams(params) : '';
    return this.request<T>(`${endpoint}${queryParams}`, { method: 'GET' });
  }

  public async post<T>(endpoint: string, data?: any, isFormData = false): Promise<T> {
    const config: RequestConfig = { method: 'POST' };

    if (isFormData) {
      config.body = data;
      config.headers = {
        ...(this.token && { 'Authorization': `Bearer ${this.token}` }),
      };
    } else {
      config.body = JSON.stringify(data);
      config.headers = this.getAuthHeaders();
    }

    return this.request<T>(endpoint, config);
  }

  public async put<T>(endpoint: string, data?: any): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  public async patch<T>(endpoint: string, data?: any): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  }

  public async delete<T = void>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'DELETE' });
  }

  // Pagination helpers
  public async getPaginated<T>(
    endpoint: string,
    params: { page?: number; size?: number; [key: string]: any } = {}
  ): Promise<PageResponse<T>> {
    const paginationParams = {
      page: params.page || 0,
      size: params.size || 20,
      ...params,
    };
    return this.get<PageResponse<T>>(endpoint, paginationParams);
  }
}

// Export the enhanced client
export const apiClient = new BaseApiClient('');

// Domain-specific client factory
export function createDomainClient(domain: string): BaseApiClient {
  return new BaseApiClient(domain);
}
