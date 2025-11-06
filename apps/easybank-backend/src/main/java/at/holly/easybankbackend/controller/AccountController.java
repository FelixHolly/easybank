package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Account;
import at.holly.easybankbackend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Account Controller
 * Handles account-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
public class AccountController {

  private final AccountRepository accountRepository;

  /**
   * Get account details for a customer
   * Requires ACCOUNT:READ authority
   */
  @GetMapping("/myAccount")
  @PreAuthorize("hasAuthority('ACCOUNT:READ')")
  public Account getAccountDetails (@RequestParam long id) {
    return accountRepository.findByCustomerId(id);
  }

}
