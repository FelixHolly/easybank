/**
 * Common Models
 * Shared data structures used across multiple features
 */

export interface SelectOption<T = string> {
  label: string;
  value: T;
  disabled?: boolean;
}

export interface TableColumn<T = any> {
  key: keyof T;
  label: string;
  sortable?: boolean;
  width?: string;
}

export interface BreadcrumbItem {
  label: string;
  url?: string;
  active?: boolean;
}

export interface MenuItem {
  id: string;
  label: string;
  icon?: string;
  url?: string;
  children?: MenuItem[];
  badge?: string | number;
}

export type SortDirection = 'asc' | 'desc';

export interface SortConfig<T> {
  field: keyof T;
  direction: SortDirection;
}
