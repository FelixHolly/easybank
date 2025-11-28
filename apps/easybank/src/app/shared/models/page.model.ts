/**
 * Spring Data Page Response Model
 * Mirrors the structure of org.springframework.data.domain.Page
 *
 * @see https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html
 */
export interface Page<T> {
  /**
   * The actual page content as array
   */
  content: T[];

  /**
   * Pagination information
   */
  pageable: Pageable;

  /**
   * Total number of elements across all pages
   */
  totalElements: number;

  /**
   * Total number of pages
   */
  totalPages: number;

  /**
   * Whether this is the last page
   */
  last: boolean;

  /**
   * The size of the page
   */
  size: number;

  /**
   * The current page number (zero-indexed)
   */
  number: number;

  /**
   * Sort information
   */
  sort: Sort;

  /**
   * Whether this is the first page
   */
  first: boolean;

  /**
   * The number of elements in this page
   */
  numberOfElements: number;

  /**
   * Whether the page is empty
   */
  empty: boolean;
}

/**
 * Spring Data Pageable interface
 */
export interface Pageable {
  sort: Sort;
  offset: number;
  pageNumber: number;
  pageSize: number;
  paged: boolean;
  unpaged: boolean;
}

/**
 * Spring Data Sort interface
 */
export interface Sort {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

/**
 * Utility: Extract content array from Page response
 *
 * @example
 * ```typescript
 * apiService.get<Page<Card>>(endpoint).pipe(
 *   map(extractPageContent),
 *   tap(cards => console.log(cards))
 * )
 * ```
 */
export function extractPageContent<T>(page: Page<T>): T[] {
  return page.content;
}
