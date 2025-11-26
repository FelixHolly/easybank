package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.AccountTransactionDto;
import at.holly.easybankbackend.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Balance Controller
 * Handles transaction and balance-related HTTP endpoints
 * Delegates business logic to BalanceService
 */
@RestController
@RequiredArgsConstructor
public class BalanceController {

  private final BalanceService balanceService;

  /**
   * Get transaction history for authenticated user
   *
   * @param authentication the authentication object containing JWT token
   * @return list of account transaction DTOs ordered by transaction date descending
   */
  @GetMapping("/myBalance")
  public List<AccountTransactionDto> getBalanceDetails(Authentication authentication) {
    return balanceService.getTransactionsForUser(authentication);
  }

}
