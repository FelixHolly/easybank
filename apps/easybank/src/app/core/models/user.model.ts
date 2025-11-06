/**
 * User Model
 * Core user data structure matching backend Customer model
 */

export interface User {
  id: number;
  name: string;
  email: string;
  mobileNumber: string;
  role: string;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData {
  name: string;
  email: string;
  mobileNumber: string;
  password: string;
  role: string;
}
