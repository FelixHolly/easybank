# Configuration

Application-wide configuration constants and settings.

## Files

- **api.config.ts** - API endpoints and configuration
- **app.config.ts** - General app configuration
- **environment.ts** - Environment-specific settings (if needed beyond standard environments)

## Usage

```typescript
export const API_CONFIG = {
  baseUrl: 'http://localhost:8080',
  endpoints: {
    auth: '/api/auth',
    customers: '/api/customers',
    // ...
  }
};
```
