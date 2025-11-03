# EasyBank Routes Documentation

Complete routing configuration for the EasyBank application.

## Route Structure

```
/                          → Redirects to /home
├── /home                  → Public landing page
├── /auth                  → Authentication routes (lazy-loaded)
│   ├── /login            → Login page ✅
│   ├── /register         → Registration page ✅
│   └── /forgot-password  → Password reset page ✅
├── /dashboard            → User dashboard (protected) ✅
├── /account              → Account management (protected)
├── /balance              → Balance information (protected)
├── /loans                → Loan management (protected)
├── /cards                → Card management (protected)
├── /contact              → Contact form (public)
├── /notices              → System notices (public)
└── /**                   → Redirects to /home (404)
```

## Route Details

### Public Routes (No Authentication)

| Route | Component | File Location | Status |
|-------|-----------|---------------|--------|
| `/home` | HomeComponent | `pages/home/home.component.ts` | ✅ Implemented |
| `/contact` | ContactComponent | `features/contact/pages/contact.component.ts` | ✅ Placeholder |
| `/notices` | NoticesComponent | `features/notices/pages/notices.component.ts` | ✅ Placeholder |

### Auth Routes (Lazy Loaded)

| Route | Component | File Location | Status |
|-------|-----------|---------------|--------|
| `/auth/login` | LoginComponent | `features/auth/pages/login/login.component.ts` | ✅ Implemented |
| `/auth/register` | RegisterComponent | `features/auth/pages/register/register.component.ts` | ✅ Implemented |
| `/auth/forgot-password` | ForgotPasswordComponent | `features/auth/pages/forgot-password/forgot-password.component.ts` | ✅ Implemented |

### Protected Routes (Requires Authentication)

| Route | Component | File Location | Status |
|-------|-----------|---------------|--------|
| `/dashboard` | DashboardHomeComponent | `features/dashboard/pages/dashboard-home/dashboard-home.component.ts` | ✅ Implemented |
| `/account` | AccountComponent | `features/account/pages/account.component.ts` | ✅ Placeholder |
| `/balance` | BalanceComponent | `features/balance/pages/balance.component.ts` | ✅ Placeholder |
| `/loans` | LoansComponent | `features/loans/pages/loans.component.ts` | ✅ Placeholder |
| `/cards` | CardsComponent | `features/cards/pages/cards.component.ts` | ✅ Placeholder |

## Route Guards

### Auth Guard
**File:** `core/guards/auth.guard.ts`

**Purpose:** Protects routes from unauthorized access

**Behavior:**
- Checks if user has authentication token in localStorage
- If authenticated: allows access
- If not authenticated: redirects to `/auth/login` with return URL

**Usage:**
```typescript
{
  path: 'dashboard',
  canActivate: [authGuard],
  component: DashboardComponent
}
```

**Protected Routes:**
- `/dashboard`
- `/account`
- `/balance`
- `/loans`
- `/cards`

## Interceptors

### Auth Interceptor
**File:** `core/interceptors/auth.interceptor.ts`

**Purpose:** Automatically adds JWT token to HTTP requests

**Behavior:**
- Retrieves token from localStorage
- Adds `Authorization: Bearer <token>` header to all requests
- Registered globally in app.config.ts

### Error Interceptor
**File:** `core/interceptors/error.interceptor.ts`

**Purpose:** Global HTTP error handling

**Behavior:**
- Catches all HTTP errors
- Handles specific status codes:
  - 401: Redirects to login
  - 403: Logs forbidden access
  - 404: Logs not found
  - 500: Logs server error
- Shows user-friendly error messages

## Navigation Flow

### First-time Visitor
```
User visits app
    ↓
Redirects to /home (landing page)
    ↓
Clicks "Login" button
    ↓
Navigates to /auth/login
    ↓
Enters credentials & submits
    ↓
AuthService.login() called
    ↓
Token stored in localStorage
    ↓
Redirects to /dashboard
```

### Returning User (with valid token)
```
User visits app
    ↓
Redirects to /home
    ↓
User navigates to /dashboard
    ↓
authGuard checks token
    ↓
Token valid → allows access
    ↓
Dashboard loads
```

### Unauthorized Access Attempt
```
User tries to visit /dashboard
    ↓
authGuard checks token
    ↓
No token found
    ↓
Redirects to /auth/login?returnUrl=/dashboard
    ↓
After successful login
    ↓
Redirects back to /dashboard
```

## Lazy Loading

Routes are optimized with lazy loading:

✅ **Auth Feature** - Loaded only when accessing /auth/*
```typescript
loadChildren: () => import('./features/auth/auth.routes')
```

✅ **Dashboard Feature** - Loaded only when accessing /dashboard
```typescript
loadChildren: () => import('./features/dashboard/dashboard.routes')
```

✅ **Other Features** - Loaded on demand
```typescript
loadComponent: () => import('./features/account/pages/account.component')
```

## Configuration Files

### app.routes.ts
Main routing configuration with all route definitions

### app.config.ts
Application configuration including:
- Router provider
- HTTP client provider
- Interceptors registration

### auth.routes.ts
Authentication feature routes (login, register, forgot-password)

### dashboard.routes.ts
Dashboard feature routes

## Page Titles

Each route has a title for browser tab:
- Home - EasyBank
- Login - EasyBank
- Register - EasyBank
- Dashboard - EasyBank
- My Account - EasyBank
- etc.

## Development URLs

When running `nx serve easybank` (default port 4200):

- http://localhost:4200 → Home
- http://localhost:4200/auth/login → Login
- http://localhost:4200/auth/register → Register
- http://localhost:4200/auth/forgot-password → Forgot Password
- http://localhost:4200/dashboard → Dashboard (requires login)
- http://localhost:4200/account → Account (requires login)
- http://localhost:4200/balance → Balance (requires login)
- http://localhost:4200/loans → Loans (requires login)
- http://localhost:4200/cards → Cards (requires login)
- http://localhost:4200/contact → Contact
- http://localhost:4200/notices → Notices

## Next Steps

### Placeholder Components to Implement

The following routes have basic placeholder components. Implement full functionality:

1. **Account Feature** (`/account`)
   - User profile management
   - Account settings
   - Security settings

2. **Balance Feature** (`/balance`)
   - Current balance display
   - Transaction history
   - Export functionality

3. **Loans Feature** (`/loans`)
   - Active loans list
   - Loan details
   - Payment history
   - Apply for new loan

4. **Cards Feature** (`/cards`)
   - Card list (credit/debit)
   - Card details
   - Transaction limits
   - Block/unblock cards

5. **Contact Feature** (`/contact`)
   - Contact form
   - Support information
   - FAQs
   - Live chat integration

6. **Notices Feature** (`/notices`)
   - System announcements
   - Important updates
   - Maintenance schedules

## Testing Routes

### Manual Testing Checklist

- [ ] Visit `/` redirects to `/home`
- [ ] `/home` displays landing page
- [ ] `/auth/login` displays login form
- [ ] `/auth/register` displays registration form
- [ ] `/auth/forgot-password` displays password reset
- [ ] Protected routes redirect to login when not authenticated
- [ ] After login, redirects to dashboard
- [ ] `/dashboard` displays dashboard with user info
- [ ] Invalid routes redirect to `/home`
- [ ] Auth interceptor adds token to requests
- [ ] Error interceptor handles 401 by redirecting to login

### Testing with Auth

```typescript
// Test login flow
1. Go to /auth/login
2. Enter: felix@example.com / password
3. Click Login
4. Should redirect to /dashboard
5. Token should be in localStorage
6. Can now access /account, /balance, etc.

// Test logout
1. Call authService.logout()
2. Should clear localStorage
3. Should redirect to /auth/login
4. Protected routes now inaccessible
```

## Troubleshooting

### Routes not working?
1. Check `app.config.ts` has `provideRouter(appRoutes)`
2. Check `app.ts` imports `RouterModule`
3. Check `app.html` has `<router-outlet></router-outlet>`

### Auth guard not redirecting?
1. Check token in localStorage: `easybank_auth_token`
2. Check authGuard is imported in routes
3. Check StorageService is working

### Lazy loading errors?
1. Check file paths in route imports
2. Check component exports are correct
3. Run `nx build easybank` to check for errors

## Summary

✅ **Complete routing system implemented**
✅ **Auth guard protecting routes**
✅ **HTTP interceptors configured**
✅ **Lazy loading enabled**
✅ **Auth feature fully implemented**
✅ **Dashboard feature fully implemented**
✅ **Public pages implemented**
⏳ **Feature pages need implementation**

All routes are functional and ready for development!
