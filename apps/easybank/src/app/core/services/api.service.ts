import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {inject, Injectable} from '@angular/core';
import {Observable, retry, RetryConfig, timer} from 'rxjs';
import {API_CONFIG} from '../../config';
import {LoggerService} from './logger.service';

/**
 * API Service
 * Base service for making HTTP requests
 * All feature services should use this service for API calls
 * Includes automatic retry logic with exponential backoff
 */
@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private readonly baseUrl = API_CONFIG.baseUrl;
  private http = inject(HttpClient);
  private logger = inject(LoggerService);

  /**
   * Retry configuration with exponential backoff
   */
  private readonly retryConfig: RetryConfig = {
    count: API_CONFIG.retry.maxAttempts,
    delay: (error, retryCount) => {
      const delayMs = API_CONFIG.retry.delay * Math.pow(2, retryCount - 1);
      this.logger.warn(`Retry attempt ${retryCount} after ${delayMs}ms`, error);
      return timer(delayMs);
    },
    resetOnSuccess: true
  };

  /**
   * GET request with automatic retry
   */
  get<T>(endpoint: string, params?: HttpParams): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${endpoint}`, {
      params,
      withCredentials: true
    }).pipe(
      retry(this.retryConfig)
    );
  }

  /**
   * POST request with automatic retry
   */
  post<T>(endpoint: string, body: unknown, options?: { headers?: HttpHeaders }): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${endpoint}`, body, {
      ...options,
      withCredentials: true
    }).pipe(
      retry(this.retryConfig)
    );
  }

  /**
   * PUT request with automatic retry
   */
  put<T>(endpoint: string, body: unknown): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${endpoint}`, body, {
      withCredentials: true
    }).pipe(
      retry(this.retryConfig)
    );
  }

  /**
   * PATCH request with automatic retry
   */
  patch<T>(endpoint: string, body: unknown): Observable<T> {
    return this.http.patch<T>(`${this.baseUrl}${endpoint}`, body, {
      withCredentials: true
    }).pipe(
      retry(this.retryConfig)
    );
  }

  /**
   * DELETE request with automatic retry
   */
  delete<T>(endpoint: string): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${endpoint}`, {
      withCredentials: true
    }).pipe(
      retry(this.retryConfig)
    );
  }
}
