package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.AccountTransactionDto;
import at.holly.easybankbackend.dto.AccountTransactionMapper;
import at.holly.easybankbackend.model.AccountTransaction;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.AccountTransactionRepository;
import at.holly.easybankbackend.service.UserProvisioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final UserProvisioningService userProvisioningService;
  private final AccountTransactionMapper accountTransactionMapper;

  /**
   * Get transaction history for a user
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @return list of account transaction DTOs ordered by transaction date descending
   */
  @GetMapping("/myBalance")
  public List<AccountTransactionDto> getBalanceDetails(Authentication authentication) {
    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);

    List<AccountTransaction> transactions =
        accountTransactionRepository.findByUserIdOrderByTransactionDtDesc(user.getId());
    return accountTransactionMapper.toDtoList(transactions);
  }

}
