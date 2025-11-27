/**
 * Development Environment Configuration
 * This file is used during local development
 */
export const environment = {
  production: false,

  // API Configuration
  api: {
    baseUrl: 'http://localhost:8080/api/v1',
    timeout: 30000,
    retry: {
      maxAttempts: 3,
      delay: 1000,
    },
  },

  // Keycloak Configuration
  keycloak: {
    url: 'http://localhost:8180/',
    realm: 'EasyBankDev',
    clientId: 'EasyBankPublicClient',
    redirectUri: 'http://localhost:4200/dashboard',
  },

  // Feature Flags
  features: {
    enableLogging: true,
    enableDevTools: true,
  },

  // Logging
  logging: {
    level: 'debug', // 'debug' | 'info' | 'warn' | 'error'
  },
} as const;

export type Environment = typeof environment;
