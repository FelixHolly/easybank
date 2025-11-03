import { Routes } from '@angular/router';

/**
 * Auth Feature Routes
 * Lazy-loaded authentication routes
 */
export const authRoutes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then((m) => m.LoginComponent),
    title: 'Login - EasyBank',
  },
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
    redirectTo: 'login',
    pathMatch: 'full',
  },
];
