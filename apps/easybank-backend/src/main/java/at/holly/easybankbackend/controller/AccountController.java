package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Account;
import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.repository.AccountRepository;
import at.holly.easybankbackend.repository.CustomerRepository;
import at.holly.easybankbackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Account Controller
 * Handles account-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {

  private final AccountRepository accountRepository;
  private final CustomerRepository customerRepository;
  private final JwtService jwtService;

  /**
   * Get account details for a customer
   * Extracts email from JWT token claims
   */
  @GetMapping("/myAccount")
  public Account getAccountDetails(Authentication authentication) {
    log.info("GET /myAccount - Fetching account details");
    log.debug("Authentication: {}, Authorities: {}",
      authentication.getName(), authentication.getAuthorities());

    // Extract email from JWT token using JwtService
    String email = jwtService.extractEmail(authentication);

    Optional<Customer> maybeCustomer = customerRepository.findByEmail(email);
    if (maybeCustomer.isEmpty()) {
      log.error("Customer not found with email: {}", email);
      throw new RuntimeException("Customer not found with email: " + email);
    }

    log.info("Customer found: {} (ID: {})", email, maybeCustomer.get().getId());
    Account account = accountRepository.findByCustomerId(maybeCustomer.get().getId());
    log.info("Account retrieved successfully for customer: {}", email);

    return account;
  }

}
