/**
 * Production Environment Configuration
 * This file is used when building for production
 * Override these values with environment variables in your deployment
 */
export const environment = {
  production: true,

  // API Configuration - OVERRIDE WITH ENVIRONMENT VARIABLES
  api: {
    baseUrl: 'https://api.easybank.com/api/v1',
    timeout: 30000,
    retry: {
      maxAttempts: 3,
      delay: 1000,
    },
  },

  // Keycloak Configuration - OVERRIDE WITH ENVIRONMENT VARIABLES
  keycloak: {
    url: 'https://auth.easybank.com/',
    realm: 'EasyBankProd',
    clientId: 'EasyBankPublicClient',
    redirectUri: 'https://easybank.com/dashboard',
  },

  // Feature Flags
  features: {
    enableLogging: false,
    enableDevTools: false,
  },

  // Logging
  logging: {
    level: 'error', // Only log errors in production
  },
} as const;
