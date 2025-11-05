import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { ApiService } from '../../../core';
import { StorageService } from '../../../core';
import { API_CONFIG } from '../../../config';
import { APP_CONSTANTS } from '../../../config';
import {User, LoginCredentials, AuthTokens, RegisterData} from '../../../core';

/**
 * Auth Service
 * Handles authentication state and operations
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // Signal for reactive current user state
  private currentUserSignal = signal<User | null>(null);

  // Public readonly signal
  readonly currentUser = this.currentUserSignal.asReadonly();

  constructor(
    private api: ApiService,
    private storage: StorageService,
    private router: Router
  ) {
    this.loadUserFromStorage();
  }

  /**
   * Login with credentials
   */
  login(credentials: LoginCredentials): Observable<AuthTokens> {
    return this.api
      .post<AuthTokens>(API_CONFIG.endpoints.auth.login, credentials)
      .pipe(
        tap((tokens) => {
          this.storeTokens(tokens);
          // TODO: Fetch and store user profile
        })
      );
  }

  //todo return value of register
  register(credentials: RegisterData): Observable<User> {
    return this.api.post<User>(API_CONFIG.endpoints.auth.register, credentials);
  }

  /**
   * Logout current user
   */
  logout(): void {
    // Clear tokens and user data
    this.storage.removeItem(APP_CONSTANTS.storageKeys.authToken);
    this.storage.removeItem(APP_CONSTANTS.storageKeys.refreshToken);
    this.storage.removeItem(APP_CONSTANTS.storageKeys.user);

    // Reset user signal
    this.currentUserSignal.set(null);

    // Navigate to login
    this.router.navigate(['/auth/login']);
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return this.storage.hasItem(APP_CONSTANTS.storageKeys.authToken);
  }

  /**
   * Get current auth token
   */
  getToken(): string | null {
    return this.storage.getItem(APP_CONSTANTS.storageKeys.authToken);
  }

  /**
   * Store authentication tokens
   */
  private storeTokens(tokens: AuthTokens): void {
    this.storage.setItem(APP_CONSTANTS.storageKeys.authToken, tokens.accessToken);
    this.storage.setItem(APP_CONSTANTS.storageKeys.refreshToken, tokens.refreshToken);
  }

  /**
   * Load user from storage on app init
   */
  private loadUserFromStorage(): void {
    const user = this.storage.getItem<User>(APP_CONSTANTS.storageKeys.user);
    if (user) {
      this.currentUserSignal.set(user);
    }
  }

  /**
   * Store user profile
   */
  setCurrentUser(user: User): void {
    this.storage.setItem(APP_CONSTANTS.storageKeys.user, user);
    this.currentUserSignal.set(user);
  }
}
