/**
 * API Response Models
 * Standard response structures from the backend
 */

export interface ApiResponse<T> {
  data: T;
  message?: string;
  status: 'success' | 'error';
  timestamp?: Date;
}

export interface ApiError {
  message: string;
  status: number;
  error?: string;
  timestamp?: Date;
}

export interface PaginatedResponse<T> {
  data: T[];
  pagination: {
    page: number;
    pageSize: number;
    totalItems: number;
    totalPages: number;
  };
}
