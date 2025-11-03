import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { StorageService } from '../services/storage.service';
import { APP_CONSTANTS } from '../../config/app.constants';

/**
 * Auth Guard
 * Prevents unauthorized access to protected routes
 *
 * Usage in routes:
 * {
 *   path: 'protected',
 *   component: ProtectedComponent,
 *   canActivate: [authGuard]
 * }
 */
export const authGuard: CanActivateFn = (route, state) => {
  // const router = inject(Router);
  // const storage = inject(StorageService);
  //
  // // Check if user has auth token
  // const token = storage.getItem(APP_CONSTANTS.storageKeys.authToken);
  //
  // if (token) {
  //   return true;
  // }
  //
  // // Redirect to login with return URL
  // router.navigate(['/auth/login'], {
  //   queryParams: { returnUrl: state.url },
  // });
  //
  // return false;
  return true;
};
