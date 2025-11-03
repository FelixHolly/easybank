import { Routes } from '@angular/router';

/**
 * Dashboard Routes
 * User dashboard after login
 */
export const dashboardRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/dashboard-home/dashboard-home.component').then(
        (m) => m.DashboardHomeComponent
      ),
    title: 'Dashboard - EasyBank',
  },
];
