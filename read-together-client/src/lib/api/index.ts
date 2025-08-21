// Main API Export - Single entry point for all API functionality
export * from './config';
export * from './client';
export * from './models';
export * from './services';

// Convenience re-exports for common usage
export { api } from './services';
export { API_CONFIG } from './config';
export { BaseApiClient, createDomainClient } from './client';
