/**
 * API Configuration
 * Central configuration for all API endpoints and settings
 */

export const API_CONFIG = {
  // Base URL - should come from environment in production
  baseUrl: 'http://localhost:8080',

  // API Endpoints
  endpoints: {
    // Auth endpoints
    auth: {
      login: '/api/auth/login',
      logout: '/api/auth/logout',
      register: '/api/auth/register',
      refresh: '/api/auth/refresh',
    },

    // Customer endpoints
    customer: {
      profile: '/api/customer/profile',
      update: '/api/customer/update',
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

  // Request timeout in milliseconds
  timeout: 30000,

  // Retry configuration
  retry: {
    maxAttempts: 3,
    delay: 1000,
  },
} as const;

// Type-safe endpoint keys
export type ApiEndpoint = typeof API_CONFIG.endpoints;
