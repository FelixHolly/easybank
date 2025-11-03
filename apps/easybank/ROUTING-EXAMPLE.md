# Routing Configuration Example

How to configure routes with the home/dashboard structure.

## Recommended Route Structure for EasyBank

```typescript
// app.routes.ts
import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // Public routes (no layout or simple layout)
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'home',
    loadComponent: () =>
      import('./pages/home/home.component').then((m) => m.HomeComponent),
    title: 'Home - EasyBank',
  },

  // Auth routes (auth layout)
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then((m) => m.authRoutes),
  },

  // Protected routes (main layout with header/sidebar)
  {
    path: '',
    loadComponent: () =>
      import('./layouts/main-layout/main-layout.component').then(
        (m) => m.MainLayoutComponent
      ),
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./features/dashboard/dashboard.routes').then(
            (m) => m.dashboardRoutes
          ),
      },
      {
        path: 'account',
        loadChildren: () =>
          import('./features/account/account.routes').then(
            (m) => m.accountRoutes
          ),
      },
      {
        path: 'balance',
        loadChildren: () =>
          import('./features/balance/balance.routes').then(
            (m) => m.balanceRoutes
          ),
      },
      {
        path: 'loans',
        loadChildren: () =>
          import('./features/loans/loans.routes').then((m) => m.loansRoutes),
      },
      {
        path: 'cards',
        loadChildren: () =>
          import('./features/cards/cards.routes').then((m) => m.cardsRoutes),
      },
    ],
  },

  // Public but needs main layout (contact, notices)
  {
    path: '',
    loadComponent: () =>
      import('./layouts/main-layout/main-layout.component').then(
        (m) => m.MainLayoutComponent
      ),
    children: [
      {
        path: 'contact',
        loadChildren: () =>
          import('./features/contact/contact.routes').then(
            (m) => m.contactRoutes
          ),
      },
      {
        path: 'notices',
        loadChildren: () =>
          import('./features/notices/notices.routes').then(
            (m) => m.noticesRoutes
          ),
      },
    ],
  },

  // Wildcard route - 404
  {
    path: '**',
    loadComponent: () =>
      import('./pages/not-found/not-found.component').then(
        (m) => m.NotFoundComponent
      ),
    title: 'Page Not Found - EasyBank',
  },
];
```

## Alternative: Simpler Structure

If you don't need a separate public home page:

```typescript
export const routes: Routes = [
  // Redirect root to dashboard
  {
    path: '',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        loadChildren: () => import('./features/dashboard/dashboard.routes'),
      },
      // ... other routes
    ],
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes'),
  },
];
```

## Navigation Flow

### For Banking App (Recommended):

1. **Unauthenticated User**
   - `/home` → Public landing page
   - Click "Login" → `/auth/login`
   - After login → `/dashboard`

2. **Authenticated User**
   - Goes to `/` → Redirects to `/dashboard`
   - Can access `/account`, `/balance`, `/loans`, `/cards`

3. **Auth Guard Behavior**
   - Tries to access `/dashboard` without login → Redirects to `/auth/login`

## File Locations Summary

| Route | Location | Type |
|-------|----------|------|
| `/home` | `pages/home/` | Public landing |
| `/dashboard` | `features/dashboard/` | Protected dashboard |
| `/auth/*` | `features/auth/` | Auth pages |
| `/account` | `features/account/` | Protected feature |
| `/balance` | `features/balance/` | Protected feature |
| `/loans` | `features/loans/` | Protected feature |
| `/cards` | `features/cards/` | Protected feature |
| `/contact` | `features/contact/` | Public/semi-public |
| `/notices` | `features/notices/` | Public/semi-public |

## Quick Reference

**Public page (landing, marketing):**
```
pages/
└── home/
```

**Protected dashboard/home:**
```
features/
└── dashboard/
```

**Any complex feature:**
```
features/
└── feature-name/
```
