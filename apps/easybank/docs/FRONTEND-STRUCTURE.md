# EasyBank Frontend - Project Structure

This document describes the folder structure and architectural decisions for the EasyBank Angular frontend.

## Architecture Overview

The application follows a feature-based architecture with clear separation of concerns:

```
src/
├── app/
│   ├── core/                    # Singleton services, guards, interceptors
│   │   ├── services/           # App-wide services (auth, api, storage)
│   │   ├── guards/             # Route guards (auth, role)
│   │   ├── interceptors/       # HTTP interceptors (token, error)
│   │   └── models/             # Core data models
│   │
│   ├── shared/                  # Reusable components and utilities
│   │   ├── components/         # Reusable UI components
│   │   ├── directives/         # Custom directives
│   │   ├── pipes/              # Custom pipes
│   │   ├── models/             # Shared models/interfaces
│   │   └── utils/              # Helper functions
│   │
│   ├── features/                # Feature modules (lazy-loaded)
│   │   ├── auth/               # Authentication feature
│   │   ├── account/            # Account management
│   │   ├── loans/              # Loans feature
│   │   ├── cards/              # Cards feature
│   │   ├── balance/            # Balance feature
│   │   ├── contact/            # Contact feature
│   │   └── notices/            # Notices feature
│   │
│   ├── layouts/                 # Layout components
│   │   ├── main-layout/        # Main app layout
│   │   └── auth-layout/        # Auth pages layout
│   │
│   ├── config/                  # Configuration files
│   │   ├── api.config.ts       # API configuration
│   │   └── app.constants.ts    # App constants
│   │
│   ├── app.routes.ts           # Main routing configuration
│   ├── app.config.ts           # App configuration
│   └── app.ts                  # Root component
│
├── assets/                      # Static assets
│   ├── images/                 # Images
│   ├── icons/                  # Icons and SVGs
│   └── fonts/                  # Custom fonts
│
├── styles.scss                 # Global styles
└── index.html                  # HTML entry point
```

## Module Organization

### Core Module (`app/core/`)

**Purpose:** Contains singleton services and app-wide functionality

**Rules:**
- Import ONLY in root AppConfig
- Services should be `providedIn: 'root'`
- Never import in feature modules

**Examples:**
- `AuthService` - Authentication state and operations
- `AuthGuard` - Protect routes requiring authentication
- `AuthInterceptor` - Add auth token to requests
- `ErrorInterceptor` - Handle HTTP errors globally

### Shared Module (`app/shared/`)

**Purpose:** Reusable components, directives, pipes used across features

**Rules:**
- Import in feature modules that need these components
- Components should be generic and stateless
- Document all inputs/outputs

**Examples:**
- `ButtonComponent` - Reusable button with variants
- `CardComponent` - Generic card container
- `LoadingSpinnerComponent` - Loading indicator
- `CurrencyPipe` - Custom currency formatting

### Features (`app/features/`)

**Purpose:** Business logic organized by domain

**Structure per feature:**
```
feature-name/
├── components/                 # Dumb/presentational components
├── pages/                      # Smart/container components
│   ├── feature-list/          # List page
│   ├── feature-detail/        # Detail page
│   └── feature-form/          # Form page
├── services/                   # Feature-specific services
├── models/                     # Feature-specific models
└── feature-name.routes.ts     # Feature routing
```

**Rules:**
- Features should be lazy-loaded
- Keep features independent
- Can import SharedModule
- Don't import other features

**Example - Auth Feature:**
```
auth/
├── pages/
│   ├── login/
│   ├── register/
│   └── forgot-password/
├── services/
│   └── auth-api.service.ts
├── models/
│   └── login-credentials.interface.ts
└── auth.routes.ts
```

### Layouts (`app/layouts/`)

**Purpose:** Define page structure for different app sections

**Rules:**
- Contains structural components with `<router-outlet>`
- Used in routing configuration
- Keep layout logic minimal

**Examples:**
- `MainLayoutComponent` - Header + Sidebar + Content + Footer
- `AuthLayoutComponent` - Centered content for login/register

### Config (`app/config/`)

**Purpose:** Centralized configuration and constants

**Files:**
- `api.config.ts` - API base URLs and endpoints
- `app.constants.ts` - App-wide constants

## Naming Conventions

### Files
- Components: `feature-name.component.ts`
- Services: `feature-name.service.ts`
- Guards: `feature-name.guard.ts`
- Interceptors: `feature-name.interceptor.ts`
- Models: `feature-name.model.ts` or `feature-name.interface.ts`

### Classes
- Components: `FeatureNameComponent`
- Services: `FeatureNameService`
- Guards: `FeatureNameGuard`
- Interceptors: `FeatureNameInterceptor`

## Component Types

### Smart Components (Containers)
- Located in `pages/` directories
- Handle state management and data fetching
- Communicate with services
- Pass data to dumb components

### Dumb Components (Presentational)
- Located in `components/` directories
- Receive data via `@Input()`
- Emit events via `@Output()`
- No direct service dependencies
- Reusable and testable

## State Management

For simple state:
- Use services with BehaviorSubject/Signal

For complex state (future):
- Consider NgRx or Signals (Angular 17+)

## Routing Strategy

```typescript
// app.routes.ts
export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: 'account',
        loadChildren: () => import('./features/account/account.routes')
      },
      // ... other protected routes
    ]
  },
  {
    path: 'auth',
    component: AuthLayoutComponent,
    children: [
      {
        path: '',
        loadChildren: () => import('./features/auth/auth.routes')
      }
    ]
  }
];
```

## Best Practices

1. **Lazy Loading** - Load features on demand to improve initial load time
2. **OnPush Change Detection** - Use for better performance
3. **Standalone Components** - Prefer standalone components (Angular 14+)
4. **Type Safety** - Use interfaces for all data models
5. **Reactive Forms** - Use for complex forms with validation
6. **RxJS** - Use observables for async operations
7. **SCSS** - Use SCSS for styling with BEM methodology
8. **Testing** - Write unit tests for services and components

## API Integration

All API calls should go through services in `core/services/`:

```typescript
// core/services/api.service.ts
@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  get<T>(endpoint: string): Observable<T> {
    return this.http.get<T>(`${API_CONFIG.baseUrl}${endpoint}`);
  }
}
```

Feature services use the ApiService:

```typescript
// features/account/services/account.service.ts
@Injectable({ providedIn: 'root' })
export class AccountService {
  constructor(private api: ApiService) {}

  getAccount(): Observable<Account> {
    return this.api.get<Account>('/myAccount');
  }
}
```

## Environment Configuration

Use Angular's environment files for environment-specific config:
- Development: `environment.development.ts`
- Production: `environment.ts`

## Development Workflow

1. Create feature in `features/`
2. Add shared components to `shared/`
3. Create services in feature or `core/`
4. Add routes with lazy loading
5. Write tests
6. Document in README if needed
