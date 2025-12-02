package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.LoanDto;
import at.holly.easybankbackend.dto.LoanSummary;
import at.holly.easybankbackend.dto.PageResponse;
import at.holly.easybankbackend.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Loans Controller
 * Handles loan-related HTTP endpoints
 * Delegates business logic to LoanService
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoansController {

  private final LoanService loanService;

  /**
   * Get loan details for authenticated user (paginated) with loan summary
   * Supports query parameters: ?page=0&size=10&sort=startDt,desc
   * Returns PageResponse with:
   * - page: Spring Data Page with loan content and pagination metadata
   * - metadata: LoanSummary with total loan amount, outstanding amount, active loan count (computed from ALL loans)
   *
   * @param authentication the authentication object containing JWT token
   * @param pageable pagination and sorting parameters (default: page 0, size 20, sorted by startDt desc)
   * @return page response with loan DTOs and loan summary
   */
  @GetMapping("/myLoans")
  public PageResponse<LoanDto, LoanSummary> getLoansDetails(
      Authentication authentication,
      @PageableDefault(size = 20, sort = "startDt", direction = Sort.Direction.DESC) Pageable pageable) {
    return loanService.getLoansWithSummary(authentication, pageable);
  }

}
