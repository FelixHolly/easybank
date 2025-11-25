import { inject } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivateFn } from '@angular/router';
import { createAuthGuard, AuthGuardData } from 'keycloak-angular';
import { LoggerService } from '../services/logger.service';
import Keycloak from 'keycloak-js';

/**
 * Auth Guard Logic
 * Checks if user is authenticated via Keycloak and has required roles
 */
const isAccessAllowed = async (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
  authData: AuthGuardData
): Promise<boolean> => {
  const { authenticated, grantedRoles, keycloak } = authData;
  const router = inject(Router);
  const logger = inject(LoggerService);

  logger.debug('[AUTH GUARD] checking access for', state.url);
  logger.navigation(`Auth Guard checking access to: ${state.url}`);
  logger.debug('Auth Guard Data:', { authenticated, realmRoles: grantedRoles.realmRoles });

  // If not authenticated, trigger Keycloak login
  // User will be redirected to Keycloak's login page and then back to the requested URL
  if (!authenticated) {
    logger.warn(`User not authenticated, triggering Keycloak login for: ${state.url}`);
    await keycloak.login({
      redirectUri: window.location.origin + state.url
    });
    return false;
  }

  // Check if route requires specific roles
  const requiredRoles = route.data['roles'] as string[] | undefined;

  if (!requiredRoles || requiredRoles.length === 0) {
    // No specific roles required, just authentication
    logger.success(`Access granted to ${state.url} (no role requirements)`);
    return true;
  }

  logger.debug(`Checking roles. Required: ${requiredRoles}, User has: ${grantedRoles.realmRoles}`);

  // Check if user has any of the required roles (realm or resource roles)
  const hasRealmRole = requiredRoles.some(role =>
    grantedRoles.realmRoles.includes(role)
  );

  const hasResourceRole = requiredRoles.some(role =>
    Object.values(grantedRoles.resourceRoles).some(roles => roles.includes(role))
  );

  if (hasRealmRole || hasResourceRole) {
    logger.success(`Access granted to ${state.url} (role check passed)`);
    return true;
  }

  // User doesn't have required role - redirect to forbidden page
  logger.error(`Access denied to ${state.url}. Required roles: ${requiredRoles}, User roles: ${grantedRoles.realmRoles}`);
  await router.navigate(['/forbidden']);
  return false;
};

/**
 * Auth Guard
 * Prevents unauthorized access to protected routes using Keycloak
 *
 * Usage in routes:
 * {
 *   path: 'protected',
 *   component: ProtectedComponent,
 *   canActivate: [authGuard],
 *   data: { roles: ['USER'] } // Optional: specify required roles
 * }
 */
export const authGuard: CanActivateFn = createAuthGuard<CanActivateFn>(isAccessAllowed);
