export interface User {
  id: number;
  name: string;
  email: string;
  mobileNumber: string;
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
}
