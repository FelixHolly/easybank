package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.AccountTransactionDto;
import at.holly.easybankbackend.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Balance Controller
 * Handles transaction and balance-related HTTP endpoints
 * Delegates business logic to BalanceService
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BalanceController {

  private final BalanceService balanceService;

  /**
   * Get transaction history for authenticated user (paginated)
   * Supports query parameters: ?page=0&size=10&sort=transactionDt,desc
   *
   * @param authentication the authentication object containing JWT token
   * @param pageable pagination and sorting parameters (default: page 0, size 20, sorted by transactionDt desc)
   * @return page of account transaction DTOs
   */
  @GetMapping("/myBalance")
  public Page<AccountTransactionDto> getBalanceDetails(
      Authentication authentication,
      @PageableDefault(size = 20, sort = "transactionDt", direction = Sort.Direction.DESC) Pageable pageable) {
    return balanceService.getTransactionsForUser(authentication, pageable);
  }

}
