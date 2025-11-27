import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { LoggerService } from '../services/logger.service';

/**
 * Error Interceptor
 * Handles HTTP errors globally
 *
 * Register in app.config.ts:
 * provideHttpClient(
 *   withInterceptors([errorInterceptor])
 * )
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const logger = inject(LoggerService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage: string;

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = `Error: ${error.error.message}`;
      } else {
        // Server-side error
        errorMessage = error.error?.message || `Error Code: ${error.status}\nMessage: ${error.message}`;

        // Handle specific status codes
        switch (error.status) {
          case 401:
            // Unauthorized - redirect to login
            logger.warn('Unauthorized access - redirecting to home');
            router.navigate(['/home']);
            break;
          case 403:
            // Forbidden
            logger.error('Access denied');
            break;
          case 404:
            // Not found
            logger.error('Resource not found');
            break;
          case 500:
            // Server error
            logger.error('Server error occurred');
            break;
        }
      }

      logger.error('HTTP Error:', errorMessage);

      // TODO: Show error notification to user
      // this.notificationService.showError(errorMessage);

      return throwError(() => error);
    })
  );
};
