package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Loan;
import at.holly.easybankbackend.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Loans Controller
 * Handles loan-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
public class LoansController {

  private final LoanRepository loanRepository;

  /**
   * Get loan details for a customer
   * Requires LOAN:READ authority
   */
  @GetMapping("/myLoans")
  @PreAuthorize("hasAuthority('LOAN:READ')")
  public List<Loan> getLoansDetails (@RequestParam long id) {
    return loanRepository.findByCustomerIdOrderByStartDtDesc(id);
  }

}
