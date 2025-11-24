import { Injectable } from '@angular/core';

/**
 * Logger Service
 * Centralized logging for the application with different log levels
 */
@Injectable({
  providedIn: 'root'
})
export class LoggerService {
  private readonly enableDebug = true; // Set to false in production
  private readonly enableTrace = true; // Set to false in production

  /**
   * Log info messages (always shown)
   */
  info(message: string, ...args: any[]): void {
    console.log(`[INFO] ${message}`, ...args);
  }

  /**
   * Log debug messages (development only)
   */
  debug(message: string, ...args: any[]): void {
    if (this.enableDebug) {
      console.log(`[DEBUG] ${message}`, ...args);
    }
  }

  /**
   * Log trace messages (verbose debugging)
   */
  trace(message: string, ...args: any[]): void {
    if (this.enableTrace) {
      console.log(`[TRACE] ${message}`, ...args);
    }
  }

  /**
   * Log warning messages
   */
  warn(message: string, ...args: any[]): void {
    console.warn(`[WARN] ${message}`, ...args);
  }

  /**
   * Log error messages
   */
  error(message: string, error?: any, ...args: any[]): void {
    console.error(`[ERROR] ${message}`, error, ...args);
  }

  /**
   * Log success messages
   */
  success(message: string, ...args: any[]): void {
    console.log(`[SUCCESS] ${message}`, ...args);
  }

  /**
   * Log authentication related messages
   */
  auth(message: string, ...args: any[]): void {
    console.log(`[AUTH] ${message}`, ...args);
  }

  /**
   * Log HTTP request/response
   */
  http(method: string, url: string, data?: any): void {
    if (this.enableDebug) {
      console.log(`[HTTP ${method}] ${url}`, data || '');
    }
  }

  /**
   * Log navigation events
   */
  navigation(message: string, ...args: any[]): void {
    if (this.enableDebug) {
      console.log(`[NAV] ${message}`, ...args);
    }
  }

  /**
   * Log Keycloak events
   */
  keycloak(message: string, ...args: any[]): void {
    console.log(`[KEYCLOAK] ${message}`, ...args);
  }

  /**
   * Group related logs together
   */
  group(title: string): void {
    if (this.enableDebug) {
      console.group(title);
    }
  }

  /**
   * End a log group
   */
  groupEnd(): void {
    if (this.enableDebug) {
      console.groupEnd();
    }
  }

  /**
   * Log with custom level
   */
  custom(level: string, message: string, ...args: any[]): void {
    console.log(`[${level}] ${message}`, ...args);
  }
}
