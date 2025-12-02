package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.AccountTransactionDto;
import at.holly.easybankbackend.dto.BalanceSummary;
import at.holly.easybankbackend.dto.PageResponse;
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

import java.math.BigDecimal;

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
   * Get transaction history for authenticated user (paginated) with summary metadata
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @param pageable pagination and sorting parameters
   * @return page response with transaction DTOs and balance summary
   */
  @Transactional(readOnly = true)
  public PageResponse<AccountTransactionDto, BalanceSummary> getTransactionsWithSummary(
      Authentication authentication, Pageable pageable) {
    log.info("Fetching transaction history with summary for authenticated user (page {}, size {})",
        pageable.getPageNumber(), pageable.getPageSize());

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);
    log.info("User found (ID: {})", user.getId());

    // Fetch paginated transactions
    Page<AccountTransaction> transactionsPage = accountTransactionRepository.findByUserId(user.getId(), pageable);
    log.info("Retrieved {} transactions (page {} of {}) for user ID: {}",
        transactionsPage.getNumberOfElements(), transactionsPage.getNumber() + 1,
        transactionsPage.getTotalPages(), user.getId());

    // Compute balance summary (aggregates from ALL transactions, not just current page)
    BigDecimal currentBalance = accountTransactionRepository.findLatestByUserId(user.getId())
        .map(AccountTransaction::getClosingBalance)
        .orElse(BigDecimal.ZERO);

    BigDecimal totalCredits = accountTransactionRepository.sumCreditsByUserId(user.getId());
    BigDecimal totalDebits = accountTransactionRepository.sumDebitsByUserId(user.getId());
    Long transactionCount = accountTransactionRepository.countByUserId(user.getId());

    BalanceSummary summary = BalanceSummary.builder()
        .currentBalance(currentBalance)
        .totalCredits(totalCredits)
        .totalDebits(totalDebits)
        .transactionCount(transactionCount)
        .build();

    log.info("Balance summary computed - current: {}, credits: {}, debits: {}, count: {}",
        currentBalance, totalCredits, totalDebits, transactionCount);

    // Map to DTOs and wrap in PageResponse
    Page<AccountTransactionDto> dtoPage = transactionsPage.map(accountTransactionMapper::toDto);
    return PageResponse.of(dtoPage, summary);
  }
}
