# Frontend Quick Start Guide

Quick reference for working with the EasyBank frontend structure.

## Project Structure at a Glance

```
src/app/
├── core/           → Singletons (services, guards, interceptors)
├── shared/         → Reusable components, pipes, directives
├── features/       → Business features (lazy-loaded)
├── layouts/        → Page layouts
└── config/         → App configuration
```

## Where Do I Put...?

| Type | Location | Example |
|------|----------|---------|
| Authentication logic | `core/services/` | `auth.service.ts` |
| Route guard | `core/guards/` | `auth.guard.ts` |
| HTTP interceptor | `core/interceptors/` | `auth.interceptor.ts` |
| Reusable button | `shared/components/button/` | `button.component.ts` |
| Custom pipe | `shared/pipes/` | `currency.pipe.ts` |
| Helper function | `shared/utils/` | `date.utils.ts` |
| Login page | `features/auth/pages/login/` | `login.component.ts` |
| Account feature | `features/account/` | Feature module |
| Main layout | `layouts/main-layout/` | `main-layout.component.ts` |
| API endpoints | `config/` | `api.config.ts` |

## Common Tasks

### Creating a New Feature

```bash
# 1. Create feature structure
cd src/app/features/my-feature
mkdir -p pages/{list,detail,form} services models

# 2. Create files
# - my-feature.routes.ts
# - services/my-feature.service.ts
# - models/my-feature.model.ts
# - pages/list/list.component.ts
```

### Adding API Endpoint

```typescript
// config/api.config.ts
export const API_CONFIG = {
  endpoints: {
    myFeature: '/api/my-feature',
  }
};
```

### Creating a Service

```typescript
// features/my-feature/services/my-feature.service.ts
import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api.service';

@Injectable({ providedIn: 'root' })
export class MyFeatureService {
  constructor(private api: ApiService) {}

  getAll() {
    return this.api.get('/api/my-feature');
  }
}
```

### Adding Route Guard

```typescript
// In routes
{
  path: 'protected',
  component: MyComponent,
  canActivate: [authGuard]
}
```

### Using Storage Service

```typescript
import { StorageService } from '@core/services/storage.service';

// In component/service
constructor(private storage: StorageService) {}

// Set item
this.storage.setItem('key', value);

// Get item
const value = this.storage.getItem<Type>('key');
```

## Key Files Created

### Configuration
- ✅ `config/api.config.ts` - API endpoints
- ✅ `config/app.constants.ts` - App constants

### Core Services
- ✅ `core/services/api.service.ts` - HTTP wrapper
- ✅ `core/services/storage.service.ts` - LocalStorage wrapper

### Core Models
- ✅ `core/models/user.model.ts` - User types
- ✅ `core/models/api-response.model.ts` - Response types

### Guards & Interceptors
- ✅ `core/guards/auth.guard.ts` - Route protection
- ✅ `core/interceptors/auth.interceptor.ts` - Add auth token
- ✅ `core/interceptors/error.interceptor.ts` - Error handling

### Utilities
- ✅ `shared/utils/form.utils.ts` - Form helpers
- ✅ `shared/utils/date.utils.ts` - Date helpers

### Example Feature
- ✅ `features/auth/` - Complete auth feature structure
- ✅ `features/auth/services/auth.service.ts` - Auth service

## Registering Interceptors

```typescript
// app.config.ts
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([authInterceptor, errorInterceptor])
    ),
    // ...
  ]
};
```

## Route Configuration Example

```typescript
// app.routes.ts
export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'account',
        loadChildren: () => import('./features/account/account.routes')
      }
    ]
  },
  {
    path: 'auth',
    component: AuthLayoutComponent,
    loadChildren: () => import('./features/auth/auth.routes')
  }
];
```

## Best Practices Checklist

- [ ] Use standalone components (Angular 14+)
- [ ] Implement lazy loading for features
- [ ] Use signals for reactive state (Angular 16+)
- [ ] Type all data with interfaces
- [ ] Use OnPush change detection
- [ ] Extract reusable logic to services
- [ ] Keep components focused and small
- [ ] Write unit tests for services
- [ ] Use reactive forms for complex forms
- [ ] Handle errors gracefully
- [ ] Implement loading states
- [ ] Add proper TypeScript types

## Development Workflow

1. **Start Development Server**
   ```bash
   nx serve easybank
   ```

2. **Create Feature** → Add in `features/`
3. **Create Reusable Component** → Add in `shared/components/`
4. **Add Route** → Update feature routes
5. **Test** → `nx test easybank`
6. **Build** → `nx build easybank`

## Need More Details?

See `FRONTEND-STRUCTURE.md` for comprehensive documentation.
