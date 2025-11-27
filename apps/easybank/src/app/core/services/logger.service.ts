import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

type LogLevel = 'debug' | 'info' | 'warn' | 'error';

/**
 * Logger Service
 * Centralized logging for the application with environment-aware log levels
 * - Development: All logs enabled
 * - Production: Only errors logged (configurable via environment)
 */
@Injectable({
  providedIn: 'root'
})
export class LoggerService {
  private readonly isProduction = environment.production;
  private readonly logLevel: LogLevel = environment.logging.level as LogLevel;

  private readonly logLevelPriority: Record<LogLevel, number> = {
    debug: 0,
    info: 1,
    warn: 2,
    error: 3
  };

  /**
   * Check if a log level should be logged based on environment configuration
   */
  private shouldLog(level: LogLevel): boolean {
    if (!environment.features.enableLogging) {
      return level === 'error'; // Always log errors
    }
    return this.logLevelPriority[level] >= this.logLevelPriority[this.logLevel];
  }

  /**
   * Log info messages
   */
  info(message: string, ...args: unknown[]): void {
    if (this.shouldLog('info')) {
      console.log(`%c[INFO]%c ${message}`, 'color: #2196F3; font-weight: bold', '', ...args);
    }
  }

  /**
   * Log debug messages (development only)
   */
  debug(message: string, ...args: unknown[]): void {
    if (this.shouldLog('debug')) {
      console.log(`%c[DEBUG]%c ${message}`, 'color: #9E9E9E; font-weight: bold', '', ...args);
    }
  }

  /**
   * Log trace messages (verbose debugging)
   */
  trace(message: string, ...args: unknown[]): void {
    if (this.shouldLog('debug')) {
      console.log(`%c[TRACE]%c ${message}`, 'color: #757575; font-weight: bold', '', ...args);
    }
  }

  /**
   * Log warning messages
   */
  warn(message: string, ...args: unknown[]): void {
    if (this.shouldLog('warn')) {
      console.warn(`%c[WARN]%c ${message}`, 'color: #FF9800; font-weight: bold', '', ...args);
    }
  }

  /**
   * Log error messages
   */
  error(message: string, error?: unknown, ...args: unknown[]): void {
    if (this.shouldLog('error')) {
      console.error(`%c[ERROR]%c ${message}`, 'color: #F44336; font-weight: bold', '', error, ...args);
    }
  }

  /**
   * Log success messages
   */
  success(message: string, ...args: unknown[]): void {
    if (this.shouldLog('info')) {
      console.log(`%c[SUCCESS]%c ${message}`, 'color: #4CAF50; font-weight: bold', '', ...args);
    }
  }

  /**
   * Log authentication related messages
   */
  auth(message: string, ...args: unknown[]): void {
    if (this.shouldLog('info')) {
      console.log(`%c[AUTH]%c ${message}`, 'color: #9C27B0; font-weight: bold', '', ...args);
    }
  }

  /**
   * Log HTTP request/response
   */
  http(method: string, url: string, data?: unknown): void {
    if (this.shouldLog('debug')) {
      console.log(`%c[HTTP ${method}]%c ${url}`, 'color: #00BCD4; font-weight: bold', '', data || '');
    }
  }

  /**
   * Log navigation events
   */
  navigation(message: string, ...args: unknown[]): void {
    if (this.shouldLog('debug')) {
      console.log(`%c[NAV]%c ${message}`, 'color: #3F51B5; font-weight: bold', '', ...args);
    }
  }

  /**
   * Log Keycloak events
   */
  keycloak(message: string, ...args: unknown[]): void {
    if (this.shouldLog('info')) {
      console.log(`%c[KEYCLOAK]%c ${message}`, 'color: #FF5722; font-weight: bold', '', ...args);
    }
  }

  /**
   * Group related logs together
   */
  group(title: string): void {
    if (this.shouldLog('debug')) {
      console.group(`%c${title}`, 'font-weight: bold; font-size: 1.1em');
    }
  }

  /**
   * End a log group
   */
  groupEnd(): void {
    if (this.shouldLog('debug')) {
      console.groupEnd();
    }
  }

  /**
   * Log with custom level
   */
  custom(level: string, message: string, ...args: unknown[]): void {
    if (this.shouldLog('info')) {
      console.log(`%c[${level}]%c ${message}`, 'color: #607D8B; font-weight: bold', '', ...args);
    }
  }

  /**
   * Log initialization message (only in development)
   */
  init(appName: string, version?: string): void {
    if (this.shouldLog('info')) {
      console.log(
        `%c${appName}%c ${version ? `v${version}` : ''}`,
        'color: #2196F3; font-size: 1.5em; font-weight: bold',
        'color: #9E9E9E'
      );
      console.log(`%cEnvironment: %c${this.isProduction ? 'Production' : 'Development'}`, 'color: #757575', this.isProduction ? 'color: #F44336; font-weight: bold' : 'color: #4CAF50; font-weight: bold');
      console.log(`%cLog Level: %c${this.logLevel}`, 'color: #757575', 'color: #2196F3; font-weight: bold');
    }
  }
}
