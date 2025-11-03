/**
 * Application Constants
 * Define app-wide constants here
 */

export const APP_CONSTANTS = {
  // App info
  appName: 'EasyBank',
  version: '1.0.0',

  // Storage keys
  storageKeys: {
    authToken: 'easybank_auth_token',
    refreshToken: 'easybank_refresh_token',
    user: 'easybank_user',
    preferences: 'easybank_preferences',
  },

  // Pagination
  pagination: {
    defaultPageSize: 10,
    pageSizeOptions: [5, 10, 25, 50, 100],
  },

  // Date formats
  dateFormats: {
    display: 'dd/MM/yyyy',
    api: 'yyyy-MM-dd',
    dateTime: 'dd/MM/yyyy HH:mm:ss',
  },

  // Validation
  validation: {
    passwordMinLength: 8,
    emailPattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
  },

  // UI
  ui: {
    toastDuration: 3000, // milliseconds
    debounceTime: 300, // milliseconds for search
  },
} as const;
