package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.model.Loan;
import at.holly.easybankbackend.repository.CustomerRepository;
import at.holly.easybankbackend.repository.LoanRepository;
import at.holly.easybankbackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Loans Controller
 * Handles loan-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
public class LoansController {

  private final LoanRepository loanRepository;
  private final CustomerRepository customerRepository;
  private final JwtService jwtService;

  /**
   * Get loan details for a customer
   * Extracts email from JWT token claims
   */
  @GetMapping("/myLoans")
  public List<Loan> getLoansDetails(Authentication authentication) {
    // Extract email from JWT token using JwtService
    String email = jwtService.extractEmail(authentication);

    Optional<Customer> maybeCustomer = customerRepository.findByEmail(email);
    if (maybeCustomer.isEmpty()) {
      throw new RuntimeException("Customer not found with email: " + email);
    }

    return loanRepository.findByCustomerIdOrderByStartDtDesc(maybeCustomer.get().getId());
  }

}
