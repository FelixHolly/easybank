import {effect, inject, Injectable, signal} from '@angular/core';
import {LoggerService, User} from '../../../core';
import Keycloak from "keycloak-js";
import {KEYCLOAK_EVENT_SIGNAL, KeycloakEventType, ReadyArgs, typeEventArgs} from 'keycloak-angular';

/**
 * Auth Service
 * Handles Keycloak-based authentication with JWT tokens
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private logger = inject(LoggerService);
  private readonly keycloak = inject(Keycloak);
  private readonly keycloakEventSignal = inject(KEYCLOAK_EVENT_SIGNAL);

  // Signal for reactive authentication state
  private authenticatedSignal = signal<boolean>(false);
  readonly authenticated = this.authenticatedSignal.asReadonly();

  // Signal for reactive current user state
  private currentUserSignal = signal<User | null>(null);
  readonly currentUser = this.currentUserSignal.asReadonly();

  constructor() {
    this.logger.auth('AuthService initialized');

    // React to Keycloak events using Signals
    effect(() => {
      const event = this.keycloakEventSignal();
      this.logger.keycloak(`Event received: ${event.type}`, event.args);

      if (event.type === KeycloakEventType.Ready) {
        const isAuthenticated = typeEventArgs<ReadyArgs>(event.args);
        this.logger.keycloak(`Keycloak Ready - Authenticated: ${isAuthenticated}`);
        this.authenticatedSignal.set(isAuthenticated);

        if (isAuthenticated) {
          this.logger.auth('User is authenticated, loading profile');
          this.loadUserProfile();
        } else {
          this.logger.auth('User is not authenticated');
        }
      }

      if (event.type === KeycloakEventType.AuthSuccess) {
        this.logger.success('Authentication successful!');
        this.authenticatedSignal.set(true);
        this.loadUserProfile();
      }

      if (event.type === KeycloakEventType.AuthLogout) {
        this.logger.auth('User logged out');
        this.authenticatedSignal.set(false);
        this.currentUserSignal.set(null);
      }

      if (event.type === KeycloakEventType.AuthRefreshError ||
          event.type === KeycloakEventType.AuthError) {
        this.logger.error(`Authentication error: ${event.type}`, event.args);
        this.authenticatedSignal.set(false);
        this.currentUserSignal.set(null);
      }

      if (event.type === KeycloakEventType.AuthRefreshSuccess) {
        this.logger.debug('Token refreshed successfully');
      }

      if (event.type === KeycloakEventType.TokenExpired) {
        this.logger.warn('Token expired, attempting refresh');
      }
    });
  }

  /**
   * Login via Keycloak
   */
  async login(redirectUri?: string): Promise<void> {
    const redirect = redirectUri || window.location.origin + '/dashboard';
    this.logger.auth(`Initiating Keycloak login with redirect: ${redirect}`);

    try {
      await this.keycloak.login({
        redirectUri: redirect
      });
    } catch (error) {
      this.logger.error('Login failed', error);
      throw error;
    }
  }

  /**
   * Logout current user via Keycloak
   */
  async logout(redirectUri?: string): Promise<void> {
    const redirect = redirectUri || "http://localhost:4200/home";
    this.logger.auth(`Logging out, redirecting to: ${redirect}`);

    try {
      await this.keycloak.logout({
        redirectUri: redirect
      });
    } catch (error) {
      this.logger.error('Logout failed', error);
      throw error;
    }
  }

  /**
   * Register new user via Keycloak
   * Redirects to Keycloak's registration page
   * After successful registration, user is automatically logged in and redirected back
   */
  async register(redirectUri?: string): Promise<void> {
    const redirect = redirectUri || window.location.origin + '/dashboard';
    this.logger.auth(`Initiating Keycloak registration with redirect: ${redirect}`);

    try {
      await this.keycloak.register({
        redirectUri: redirect
      });
    } catch (error) {
      this.logger.error('Registration redirect failed', error);
      throw error;
    }
  }

  /**
   * Check if user is authenticated via Keycloak
   */
  isAuthenticated(): boolean {
    return this.keycloak.authenticated ?? false;
  }

  /**
   * Get user email from Keycloak token
   */
  getUserEmail(): string | undefined {
    return this.keycloak.tokenParsed?.['email'] as string | undefined;
  }

  /**
   * Get username from Keycloak token
   */
  getUsername(): string | undefined {
    return this.keycloak.tokenParsed?.['preferred_username'] as string | undefined;
  }

  /**
   * Get user roles from Keycloak token
   */
  getUserRoles(): string[] {
    return this.keycloak.tokenParsed?.['realm_access']?.['roles'] || [];
  }

  /**
   * Check if user has a specific role
   */
  hasRole(role: string): boolean {
    return this.keycloak.hasRealmRole(role);
  }

  /**
   * Get current access token
   */
  getToken(): string | undefined {
    return this.keycloak.token;
  }

  /**
   * Load user profile from Keycloak
   */
  private async loadUserProfile(): Promise<void> {
    this.logger.debug('Loading user profile from Keycloak...');

    try {
      const profile = await this.keycloak.loadUserProfile();
      this.logger.debug('Keycloak profile loaded:', profile);

      const roles = this.getUserRoles();
      this.logger.debug('User roles:', roles);

      const user: User = {
        email: profile.email || '',
        name: profile.firstName && profile.lastName
          ? `${profile.firstName} ${profile.lastName}`
          : profile.username || '',
        role: roles[0] || 'USER'
      };

      this.logger.success('User profile loaded:', user);
      this.currentUserSignal.set(user);
    } catch (error) {
      this.logger.error('Failed to load user profile', error);
      this.currentUserSignal.set(null);
    }
  }

  /**
   * Get current user
   */
  getCurrentUser(): User | null {
    return this.currentUserSignal();
  }
}
