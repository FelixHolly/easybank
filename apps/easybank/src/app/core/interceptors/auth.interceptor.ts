import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { StorageService } from '../services/storage.service';
import { APP_CONSTANTS } from '../../config/app.constants';

/**
 * Auth Interceptor
 * Automatically adds authentication token to outgoing HTTP requests
 *
 * Register in app.config.ts:
 * provideHttpClient(
 *   withInterceptors([authInterceptor])
 * )
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // const storage = inject(StorageService);
  //
  // // Get auth token from storage
  // const token = storage.getItem<string>(APP_CONSTANTS.storageKeys.authToken);
  //
  // // Clone request and add Authorization header if token exists
  // if (token) {
  //   req = req.clone({
  //     setHeaders: {
  //       Authorization: `Bearer ${token}`,
  //     },
  //   });
  // }

  return next(req);
};
