import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Reusable Pagination Component
 *
 * Displays pagination controls with:
 * - Previous/Next buttons
 * - Page number buttons
 * - Page range info
 * - Page size selector
 *
 * @example
 * ```html
 * <app-pagination
 *   [currentPage]="balanceStore.currentPage()"
 *   [totalPages]="balanceStore.totalPages()"
 *   [pageNumbers]="balanceStore.pageNumbers()"
 *   [hasPreviousPage]="balanceStore.hasPreviousPage()"
 *   [hasNextPage]="balanceStore.hasNextPage()"
 *   [pageRange]="balanceStore.pageRange()"
 *   [pageSize]="balanceStore.pageSize()"
 *   (pageChange)="balanceStore.goToPage($event)"
 *   (previousPage)="balanceStore.previousPage()"
 *   (nextPage)="balanceStore.nextPage()"
 *   (pageSizeChange)="balanceStore.changePageSize($event)"
 * />
 * ```
 */
@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.scss'
})
export class PaginationComponent {
  // Inputs
  currentPage = input.required<number>();
  totalPages = input.required<number>();
  pageNumbers = input.required<number[]>();
  hasPreviousPage = input.required<boolean>();
  hasNextPage = input.required<boolean>();
  pageRange = input.required<string>();
  pageSize = input.required<number>();
  showPageSizeSelector = input<boolean>(true);

  // Outputs
  pageChange = output<number>();
  previousPage = output<void>();
  nextPage = output<void>();
  pageSizeChange = output<number>();

  // Available page sizes
  pageSizes = [10, 20, 50, 100];

  /**
   * Handle page change
   */
  onPageChange(page: number): void {
    this.pageChange.emit(page);
  }

  /**
   * Handle previous page
   */
  onPreviousPage(): void {
    this.previousPage.emit();
  }

  /**
   * Handle next page
   */
  onNextPage(): void {
    this.nextPage.emit();
  }

  /**
   * Handle page size change
   */
  onPageSizeChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const size = parseInt(select.value, 10);
    this.pageSizeChange.emit(size);
  }
}
