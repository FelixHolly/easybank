package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.AccountTransaction;
import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.repository.AccountTransactionRepository;
import at.holly.easybankbackend.repository.CustomerRepository;
import at.holly.easybankbackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Balance Controller
 * Handles transaction and balance-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
public class BalanceController {

  private final AccountTransactionRepository accountTransactionRepository;
  private final CustomerRepository customerRepository;
  private final JwtService jwtService;

  /**
   * Get transaction history for a customer
   * Extracts email from JWT token claims
   */
  @GetMapping("/myBalance")
  public List<AccountTransaction> getBalanceDetails(Authentication authentication) {
    // Extract email from JWT token using JwtService
    String email = jwtService.extractEmail(authentication);
    System.out.println(email);

    Optional<Customer> maybeCustomer = customerRepository.findByEmail(email);
    if (maybeCustomer.isEmpty()) {
      throw new RuntimeException("Customer not found with email: " + email);
    }

    return accountTransactionRepository.findByCustomerIdOrderByTransactionDtDesc(maybeCustomer.get().getId());
  }

}
