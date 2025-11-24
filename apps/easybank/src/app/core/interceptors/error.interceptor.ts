import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

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

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unknown error occurred';

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
            router.navigate(['/home']);
            break;
          case 403:
            // Forbidden
            console.error('Access denied');
            break;
          case 404:
            // Not found
            console.error('Resource not found');
            break;
          case 500:
            // Server error
            console.error('Server error occurred');
            break;
        }
      }

      console.error('HTTP Error:', errorMessage);

      // TODO: Show error notification to user
      // this.notificationService.showError(errorMessage);

      return throwError(() => error);
    })
  );
};
