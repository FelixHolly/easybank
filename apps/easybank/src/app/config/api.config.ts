import { environment } from '../../environments/environment';

/**
 * API Configuration
 * Central configuration for all API endpoints and settings
 * Now uses environment-based configuration
 */

export const API_CONFIG = {
  // Base URL from environment
  baseUrl: environment.api.baseUrl,

  // API Endpoints - all now include /api/v1 prefix via baseUrl
  endpoints: {
    // Auth endpoints
    auth: {
      user: '/user',
      logout: '/logout',
      register: '/register',
    },

    // Account endpoints
    account: '/myAccount',

    // Financial endpoints
    balance: '/myBalance',
    loans: '/myLoans',
    cards: '/myCards',

    // Public endpoints
    contact: '/contact',
    notices: '/notices',
  },

  // Request timeout from environment
  timeout: environment.api.timeout,

  // Retry configuration from environment
  retry: environment.api.retry,
} as const;

// Type-safe endpoint keys
export type ApiEndpoint = typeof API_CONFIG.endpoints;
