import { Routes } from '@angular/router';
import { authGuard } from './core';

export const appRoutes: Routes = [
  // Default redirect to home
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },

  // Public home/landing page
  {
    path: 'home',
    loadComponent: () =>
      import('./pages/home/home.component').then((m) => m.HomeComponent),
    title: 'Home - EasyBank',
  },

  // Auth routes (login, register, forgot-password)
  {
    path: 'auth',
    loadChildren: () =>
      import('./features/auth/auth.routes').then((m) => m.authRoutes),
  },

  // Protected routes with authentication
  {
    path: '',
    canActivate: [authGuard],
    children: [
      // Dashboard (user home after login)
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./features/dashboard/dashboard.routes').then(
            (m) => m.dashboardRoutes
          ),
      },

      // Account feature
      {
        path: 'account',
        loadComponent: () =>
          import('./features/account/pages/account.component').then(
            (m) => m.AccountComponent
          ),
        title: 'My Account - EasyBank',
      },

      // Balance feature
      {
        path: 'balance',
        loadComponent: () =>
          import('./features/balance/pages/balance.component').then(
            (m) => m.BalanceComponent
          ),
        title: 'Balance - EasyBank',
      },

      // Loans feature
      {
        path: 'loans',
        loadComponent: () =>
          import('./features/loans/pages/loans.component').then(
            (m) => m.LoansComponent
          ),
        title: 'My Loans - EasyBank',
      },

      // Cards feature
      {
        path: 'cards',
        loadComponent: () =>
          import('./features/cards/pages/cards.component').then(
            (m) => m.CardsComponent
          ),
        title: 'My Cards - EasyBank',
      },
    ],
  },

  // Public routes (no authentication required)
  {
    path: '',
    children: [
      // Contact page
      {
        path: 'contact',
        loadComponent: () =>
          import('./features/contact/pages/contact.component').then(
            (m) => m.ContactComponent
          ),
        title: 'Contact Us - EasyBank',
      },

      // Notices page
      {
        path: 'notices',
        loadComponent: () =>
          import('./features/notices/pages/notices.component').then(
            (m) => m.NoticesComponent
          ),
        title: 'Notices - EasyBank',
      },
    ],
  },

  // Wildcard route - 404 page
  {
    path: '**',
    redirectTo: 'home',
  },
];
