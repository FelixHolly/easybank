import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Auth Interceptor
 * Handles Basic Auth and CSRF token for session-based authentication
 *
 * Register in app.config.ts:
 * provideHttpClient(
 *   withInterceptors([authInterceptor])
 * )
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Extract CSRF token from cookie
  const csrfToken = getCookie('XSRF-TOKEN');

  // For non-GET requests, add CSRF token header
  if (csrfToken && req.method !== 'GET') {
    req = req.clone({
      setHeaders: {
        'X-XSRF-TOKEN': csrfToken,
      },
    });
  }

  return next(req);
};

/**
 * Get cookie value by name
 */
function getCookie(name: string): string | null {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) {
    return parts.pop()?.split(';').shift() || null;
  }
  return null;
}
