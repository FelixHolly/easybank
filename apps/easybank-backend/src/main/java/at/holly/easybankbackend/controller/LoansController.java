package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.LoanDto;
import at.holly.easybankbackend.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Loans Controller
 * Handles loan-related HTTP endpoints
 * Delegates business logic to LoanService
 */
@RestController
@RequiredArgsConstructor
public class LoansController {

  private final LoanService loanService;

  /**
   * Get loan details for authenticated user
   *
   * @param authentication the authentication object containing JWT token
   * @return list of loan DTOs ordered by start date descending
   */
  @GetMapping("/myLoans")
  public List<LoanDto> getLoansDetails(Authentication authentication) {
    return loanService.getLoansForUser(authentication);
  }

}
