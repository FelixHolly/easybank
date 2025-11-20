package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Account;
import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.repository.AccountRepository;
import at.holly.easybankbackend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Account Controller
 * Handles account-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
public class AccountController {

  private final AccountRepository accountRepository;
  private final CustomerRepository customerRepository;

  /**
   * Get account details for a customer
   * Requires ACCOUNT:READ authority
   */
  @GetMapping("/myAccount")
  public Account getAccountDetails (@RequestParam String email) {
    Optional<Customer> maybeCustomer = customerRepository.findByEmail(email);
    if (maybeCustomer.isEmpty()) {
      throw new RuntimeException("Customer not found");
    }

    return accountRepository.findByCustomerId(maybeCustomer.get().getId());
  }

}
