import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { ApiService } from '../../../core';
import { StorageService } from '../../../core';
import { API_CONFIG } from '../../../config';
import { APP_CONSTANTS } from '../../../config';
import { User, LoginCredentials, RegisterData } from '../../../core';

/**
 * Auth Service
 * Handles session-based authentication with HTTP Basic Auth
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private api = inject(ApiService);
  private http = inject(HttpClient);
  private storage = inject(StorageService);
  private router = inject(Router);

  // Signal for reactive current user state
  private currentUserSignal = signal<User | null>(this.storage.getItem<User>(APP_CONSTANTS.storageKeys.user));

  // Public readonly signal
  readonly currentUser = this.currentUserSignal.asReadonly();

  /**
   * Login with credentials using HTTP Basic Auth
   */
  login(credentials: LoginCredentials): Observable<User> {
    // Create Basic Auth header
    const basicAuth = btoa(`${credentials.email}:${credentials.password}`);
    const headers = new HttpHeaders({
      'Authorization': `Basic ${basicAuth}`
    });

    // Call /user endpoint with Basic Auth to authenticate and get user details
    return this.http.get<User>(
      `${API_CONFIG.baseUrl}${API_CONFIG.endpoints.auth.user}`,
      {
        headers,
        withCredentials: true
      }
    ).pipe(
      tap((user) => {
        this.setCurrentUser(user);
      })
    );
  }

  /**
   * Register new user
   */
  register(credentials: RegisterData): Observable<string> {
    return this.api.post<string>(API_CONFIG.endpoints.auth.register, credentials);
  }

  /**
   * Logout current user
   */
  logout(): Observable<string> {
    return this.api.post<string>(API_CONFIG.endpoints.auth.logout, {}).pipe(
      tap(() => {
        // Clear user data
        this.storage.removeItem(APP_CONSTANTS.storageKeys.user);

        // Reset user signal
        this.currentUserSignal.set(null);

        // Navigate to login
        this.router.navigate(['/auth/login']);
      })
    );
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return this.storage.hasItem(APP_CONSTANTS.storageKeys.user);
  }

  /**
   * Get current user from storage
   */
  getCurrentUser(): User | null {
    return this.storage.getItem<User>(APP_CONSTANTS.storageKeys.user);
  }

  /**
   * Store user profile
   */
  setCurrentUser(user: User): void {
    this.storage.setItem(APP_CONSTANTS.storageKeys.user, user);
    this.currentUserSignal.set(user);
  }
}
