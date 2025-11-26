package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.AccountTransactionDto;
import at.holly.easybankbackend.mapper.AccountTransactionMapper;
import at.holly.easybankbackend.model.AccountTransaction;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.AccountTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Balance Service
 * Handles business logic for transaction and balance operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

  private final AccountTransactionRepository accountTransactionRepository;
  private final UserProvisioningService userProvisioningService;
  private final AccountTransactionMapper accountTransactionMapper;

  /**
   * Get transaction history for authenticated user
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @return list of account transaction DTOs ordered by transaction date descending
   */
  @Transactional(readOnly = true)
  public List<AccountTransactionDto> getTransactionsForUser(Authentication authentication) {
    log.info("Fetching transaction history for authenticated user");

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);
    log.info("User found (ID: {})", user.getId());

    // Fetch transactions
    List<AccountTransaction> transactions =
        accountTransactionRepository.findByUserIdOrderByTransactionDtDesc(user.getId());
    log.info("Retrieved {} transactions for user ID: {}", transactions.size(), user.getId());

    return accountTransactionMapper.toDtoList(transactions);
  }
}
