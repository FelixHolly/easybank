import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {ApiService} from './api.service';
import {LoggerService} from './logger.service';
import {API_CONFIG} from '../../config';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;
  let loggerSpy: jest.Mocked<LoggerService>;

  beforeEach(() => {
    // Create spy for LoggerService
    const spy = {
      warn: jest.fn(),
      error: jest.fn(),
    } as unknown as jest.Mocked<LoggerService>;

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ApiService,
        { provide: LoggerService, useValue: spy }
      ]
    });

    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
    loggerSpy = TestBed.inject(LoggerService) as jest.Mocked<LoggerService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('GET requests', () => {
    it('should make a GET request with correct URL', () => {
      const testEndpoint = '/test';
      const mockData = { id: 1, name: 'Test' };

      service.get(testEndpoint).subscribe(data => {
        expect(data).toEqual(mockData);
      });

      const req = httpMock.expectOne(`${API_CONFIG.baseUrl}${testEndpoint}`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockData);
    });

    it('should include query params when provided', () => {
      const testEndpoint = '/test';
      const mockData = { results: [] };
      const params = new URLSearchParams({ page: '1', limit: '10' });

      service.get(testEndpoint, params as any).subscribe();

      const req = httpMock.expectOne(request =>
        request.url.includes(testEndpoint) &&
        request.params.get('page') === '1' &&
        request.params.get('limit') === '10'
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockData);
    });

    it('should retry failed requests with exponential backoff', (done) => {
      const testEndpoint = '/test';
      const mockData = { success: true };
      let attemptCount = 0;

      service.get(testEndpoint).subscribe({
        next: (data) => {
          expect(data).toEqual(mockData);
          expect(attemptCount).toBe(3); // Initial + 2 retries
          done();
        }
      });

      // Fail first two attempts
      for (let i = 0; i < 2; i++) {
        const req = httpMock.expectOne(`${API_CONFIG.baseUrl}${testEndpoint}`);
        attemptCount++;
        req.flush('Error', { status: 500, statusText: 'Server Error' });
      }

      // Succeed on third attempt
      setTimeout(() => {
        const req = httpMock.expectOne(`${API_CONFIG.baseUrl}${testEndpoint}`);
        attemptCount++;
        req.flush(mockData);
      }, 100);
    });
  });

  describe('POST requests', () => {
    it('should make a POST request with body', () => {
      const testEndpoint = '/test';
      const requestBody = { name: 'New Item' };
      const mockResponse = { id: 1, ...requestBody };

      service.post(testEndpoint, requestBody).subscribe(data => {
        expect(data).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${API_CONFIG.baseUrl}${testEndpoint}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(requestBody);
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockResponse);
    });

    it('should include custom headers when provided', () => {
      const testEndpoint = '/test';
      const requestBody = { data: 'test' };
      const customHeaders = { 'X-Custom-Header': 'value' };

      service.post(testEndpoint, requestBody, { headers: customHeaders as any }).subscribe();

      const req = httpMock.expectOne(`${API_CONFIG.baseUrl}${testEndpoint}`);
      expect(req.request.headers.get('X-Custom-Header')).toBe('value');
      req.flush({});
    });
  });

  describe('PUT requests', () => {
    it('should make a PUT request', () => {
      const testEndpoint = '/test/1';
      const requestBody = { name: 'Updated Item' };
      const mockResponse = { id: 1, ...requestBody };

      service.put(testEndpoint, requestBody).subscribe(data => {
        expect(data).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${API_CONFIG.baseUrl}${testEndpoint}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(requestBody);
      req.flush(mockResponse);
    });
  });

  describe('PATCH requests', () => {
    it('should make a PATCH request', () => {
      const testEndpoint = '/test/1';
      const requestBody = { status: 'active' };
      const mockResponse = { id: 1, name: 'Item', ...requestBody };

      service.patch(testEndpoint, requestBody).subscribe(data => {
        expect(data).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${API_CONFIG.baseUrl}${testEndpoint}`);
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual(requestBody);
      req.flush(mockResponse);
    });
  });

  describe('DELETE requests', () => {
    it('should make a DELETE request', () => {
      const testEndpoint = '/test/1';

      service.delete(testEndpoint).subscribe(data => {
        expect(data).toEqual({});
      });

      const req = httpMock.expectOne(`${API_CONFIG.baseUrl}${testEndpoint}`);
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });
  });
});
