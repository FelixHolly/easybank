import { Routes } from '@angular/router';

/**
 * Auth Feature Routes
 * Lazy-loaded authentication routes
 * Note: Login is handled by Keycloak directly
 */
export const authRoutes: Routes = [
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/register/register.component').then((m) => m.RegisterComponent),
    title: 'Register - EasyBank',
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./pages/forgot-password/forgot-password.component').then(
        (m) => m.ForgotPasswordComponent
      ),
    title: 'Forgot Password - EasyBank',
  },
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full',
  },
];
