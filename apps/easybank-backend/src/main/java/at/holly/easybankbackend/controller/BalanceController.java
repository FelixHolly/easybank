package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.AccountTransaction;
import at.holly.easybankbackend.repository.AccountTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Balance Controller
 * Handles transaction and balance-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
public class BalanceController {

  private final AccountTransactionRepository accountTransactionRepository;

  /**
   * Get transaction history for a customer
   * Requires TRANSACTION:READ authority
   */
  @GetMapping("/myBalance")
  @PreAuthorize("hasAuthority('TRANSACTION:READ')")
  public List<AccountTransaction> getBalanceDetails (@RequestParam long id) {
    return accountTransactionRepository.findByCustomerIdOrderByTransactionDtDesc(id);
  }

}
