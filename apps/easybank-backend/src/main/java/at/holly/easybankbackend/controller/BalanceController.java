package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.AccountTransaction;
import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.repository.AccountTransactionRepository;
import at.holly.easybankbackend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  /**
   * Get transaction history for a customer
   * Requires TRANSACTION:READ authority
   */
  @GetMapping("/myBalance")
  public List<AccountTransaction> getBalanceDetails (@RequestParam String email) {
    Optional<Customer> maybeCustomer = customerRepository.findByEmail(email);
    if (maybeCustomer.isEmpty()) {
      throw new RuntimeException("Customer not found");
    }

    return accountTransactionRepository.findByCustomerIdOrderByTransactionDtDesc(maybeCustomer.get().getId());
  }

}
