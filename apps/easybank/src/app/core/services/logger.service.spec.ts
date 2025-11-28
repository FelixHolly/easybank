import { TestBed } from '@angular/core/testing';
import { LoggerService } from './logger.service';

describe('LoggerService', () => {
  let service: LoggerService;
  let consoleLogSpy: jest.SpyInstance;
  let consoleWarnSpy: jest.SpyInstance;
  let consoleErrorSpy: jest.SpyInstance;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LoggerService]
    });

    service = TestBed.inject(LoggerService);

    // Spy on console methods
    consoleLogSpy = jest.spyOn(console, 'log').mockImplementation();
    consoleWarnSpy = jest.spyOn(console, 'warn').mockImplementation();
    consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
  });

  afterEach(() => {
    consoleLogSpy.mockRestore();
    consoleWarnSpy.mockRestore();
    consoleErrorSpy.mockRestore();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Log Level Management', () => {
    it('should log info messages when log level permits', () => {
      service.info('Test info message');
      expect(consoleLogSpy).toHaveBeenCalled();
      const args = consoleLogSpy.mock.calls[consoleLogSpy.mock.calls.length - 1];
      expect(args[0]).toContain('[INFO]');
      expect(args[0]).toContain('Test info message');
    });

    it('should log debug messages when log level permits', () => {
      service.debug('Test debug message');
      expect(consoleLogSpy).toHaveBeenCalled();
      const args = consoleLogSpy.mock.calls[consoleLogSpy.mock.calls.length - 1];
      expect(args[0]).toContain('[DEBUG]');
      expect(args[0]).toContain('Test debug message');
    });

    it('should log warning messages', () => {
      service.warn('Test warning message');
      expect(consoleWarnSpy).toHaveBeenCalled();
      const args = consoleWarnSpy.mock.calls[consoleWarnSpy.mock.calls.length - 1];
      expect(args[0]).toContain('[WARN]');
      expect(args[0]).toContain('Test warning message');
    });

    it('should log error messages', () => {
      const error = new Error('Test error');
      service.error('Test error message', error);
      expect(consoleErrorSpy).toHaveBeenCalled();
      const args = consoleErrorSpy.mock.calls[consoleErrorSpy.mock.calls.length - 1];
      expect(args[0]).toContain('[ERROR]');
      expect(args[0]).toContain('Test error message');
      expect(args[3]).toBe(error);
    });

    it('should log success messages', () => {
      service.success('Test success message');
      expect(consoleLogSpy).toHaveBeenCalled();
      const args = consoleLogSpy.mock.calls[consoleLogSpy.mock.calls.length - 1];
      expect(args[0]).toContain('[SUCCESS]');
      expect(args[0]).toContain('Test success message');
    });
  });

  describe('Specialized Logging', () => {
    it('should log authentication messages', () => {
      service.auth('User logged in');
      expect(consoleLogSpy).toHaveBeenCalled();
      const args = consoleLogSpy.mock.calls[consoleLogSpy.mock.calls.length - 1];
      expect(args[0]).toContain('[AUTH]');
      expect(args[0]).toContain('User logged in');
    });

    it('should log HTTP messages', () => {
      const data = { id: 1 };
      service.http('GET', '/api/v1/user', data);
      expect(consoleLogSpy).toHaveBeenCalled();
      const args = consoleLogSpy.mock.calls[consoleLogSpy.mock.calls.length - 1];
      expect(args[0]).toContain('[HTTP GET]');
      expect(args[0]).toContain('/api/v1/user');
    });

    it('should log navigation messages', () => {
      service.navigation('Navigating to /dashboard');
      expect(consoleLogSpy).toHaveBeenCalled();
      const args = consoleLogSpy.mock.calls[consoleLogSpy.mock.calls.length - 1];
      expect(args[0]).toContain('[NAV]');
      expect(args[0]).toContain('Navigating to /dashboard');
    });

    it('should log Keycloak messages', () => {
      service.keycloak('Token refreshed');
      expect(consoleLogSpy).toHaveBeenCalled();
      const args = consoleLogSpy.mock.calls[consoleLogSpy.mock.calls.length - 1];
      expect(args[0]).toContain('[KEYCLOAK]');
      expect(args[0]).toContain('Token refreshed');
    });

    it('should log custom messages with custom level', () => {
      service.custom('CUSTOM', 'Custom message');
      expect(consoleLogSpy).toHaveBeenCalled();
      const args = consoleLogSpy.mock.calls[consoleLogSpy.mock.calls.length - 1];
      expect(args[0]).toContain('[CUSTOM]');
      expect(args[0]).toContain('Custom message');
    });
  });

  describe('Log Grouping', () => {
    let consoleGroupSpy: jest.SpyInstance;
    let consoleGroupEndSpy: jest.SpyInstance;

    beforeEach(() => {
      consoleGroupSpy = jest.spyOn(console, 'group').mockImplementation();
      consoleGroupEndSpy = jest.spyOn(console, 'groupEnd').mockImplementation();
    });

    afterEach(() => {
      consoleGroupSpy.mockRestore();
      consoleGroupEndSpy.mockRestore();
    });

    it('should create log groups', () => {
      service.group('Test Group');
      expect(consoleGroupSpy).toHaveBeenCalled();
      const args = consoleGroupSpy.mock.calls[0];
      expect(args[0]).toContain('Test Group');
    });

    it('should end log groups', () => {
      service.groupEnd();
      expect(consoleGroupEndSpy).toHaveBeenCalled();
    });
  });

  describe('Initialization Logging', () => {
    it('should log application initialization', () => {
      service.init('EasyBank', '1.0.0');
      expect(consoleLogSpy).toHaveBeenCalledTimes(3);

      // Check app name and version
      const firstCall = consoleLogSpy.mock.calls[0];
      expect(firstCall[0]).toContain('EasyBank');
      expect(firstCall[0]).toContain('v1.0.0');

      // Check environment log
      const secondCall = consoleLogSpy.mock.calls[1];
      expect(secondCall[0]).toContain('Environment:');

      // Check log level log
      const thirdCall = consoleLogSpy.mock.calls[2];
      expect(thirdCall[0]).toContain('Log Level:');
    });

    it('should handle initialization without version', () => {
      service.init('EasyBank');
      expect(consoleLogSpy).toHaveBeenCalled();
      const firstCall = consoleLogSpy.mock.calls[0];
      expect(firstCall[0]).toContain('EasyBank');
    });
  });

  describe('Additional Arguments', () => {
    it('should pass additional arguments to console methods', () => {
      const arg1 = { key: 'value' };
      const arg2 = [1, 2, 3];

      service.info('Test with args', arg1, arg2);
      expect(consoleLogSpy).toHaveBeenCalled();
      const args = consoleLogSpy.mock.calls[consoleLogSpy.mock.calls.length - 1];
      // Args start at index 3 (after template, style1, style2)
      expect(args[3]).toBe(arg1);
      expect(args[4]).toBe(arg2);
    });
  });
});
