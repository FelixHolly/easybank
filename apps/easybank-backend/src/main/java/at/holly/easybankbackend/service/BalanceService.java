package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.AccountTransactionDto;
import at.holly.easybankbackend.mapper.AccountTransactionMapper;
import at.holly.easybankbackend.model.AccountTransaction;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.AccountTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
   * Get transaction history for authenticated user (paginated)
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @param pageable pagination and sorting parameters
   * @return page of account transaction DTOs
   */
  @Transactional(readOnly = true)
  public Page<AccountTransactionDto> getTransactionsForUser(Authentication authentication, Pageable pageable) {
    log.info("Fetching transaction history for authenticated user (page {}, size {})",
        pageable.getPageNumber(), pageable.getPageSize());

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);
    log.info("User found (ID: {})", user.getId());

    // Fetch transactions with pagination
    Page<AccountTransaction> transactions = accountTransactionRepository.findByUserId(user.getId(), pageable);
    log.info("Retrieved {} transactions (page {} of {}) for user ID: {}",
        transactions.getNumberOfElements(), transactions.getNumber() + 1, transactions.getTotalPages(), user.getId());

    return transactions.map(accountTransactionMapper::toDto);
  }
}
