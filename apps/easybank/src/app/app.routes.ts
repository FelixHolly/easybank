import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const appRoutes: Routes = [
  // Default redirect to home
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },

  // Public routes
  {
    path: 'home',
    loadComponent: () =>
      import('./pages/home/home.component').then((m) => m.HomeComponent),
    title: 'Home - EasyBank',
  },
  {
    path: 'contact',
    loadComponent: () =>
      import('./features/contact/pages/contact.component').then(
        (m) => m.ContactComponent
      ),
    title: 'Contact Us - EasyBank',
  },
  {
    path: 'notices',
    loadComponent: () =>
      import('./features/notices/pages/notices.component').then(
        (m) => m.NoticesComponent
      ),
    title: 'Notices - EasyBank',
  },

  // Protected routes with authentication
  {
    path: '',
    canActivate: [authGuard],
    data: { roles: ['USER'] },
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
        data: { roles: ['USER'] },
        loadComponent: () =>
          import('./features/account/pages/account.component').then(
            (m) => m.AccountComponent
          ),
        title: 'My Account - EasyBank',
      },
      {
        path: 'balance',
        data: { roles: ['USER'] },
        loadComponent: () =>
          import('./features/balance/pages/balance.component').then(
            (m) => m.BalanceComponent
          ),
        title: 'Balance - EasyBank',
      },
      {
        path: 'loans',
        data: { roles: ['USER'] },
        loadComponent: () =>
          import('./features/loans/pages/loans.component').then(
            (m) => m.LoansComponent
          ),
        title: 'My Loans - EasyBank',
      },
      {
        path: 'cards',
        data: { roles: ['USER'] },
        loadComponent: () =>
          import('./features/cards/pages/cards.component').then(
            (m) => m.CardsComponent
          ),
        title: 'My Cards - EasyBank',
      },
    ],
  },

  // Wildcard route - 404 page
  {
    path: '**',
    redirectTo: 'home',
  },
];

