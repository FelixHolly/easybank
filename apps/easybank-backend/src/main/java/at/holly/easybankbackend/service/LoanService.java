package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.LoanDto;
import at.holly.easybankbackend.dto.LoanSummary;
import at.holly.easybankbackend.dto.PageResponse;
import at.holly.easybankbackend.mapper.LoanMapper;
import at.holly.easybankbackend.model.Loan;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Loan Service
 * Handles business logic for loan operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

  private final LoanRepository loanRepository;
  private final UserProvisioningService userProvisioningService;
  private final LoanMapper loanMapper;

  /**
   * Get loan details for authenticated user (paginated) with summary metadata
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @param pageable pagination and sorting parameters
   * @return page response with loan DTOs and loan summary
   */
  @Transactional(readOnly = true)
  public PageResponse<LoanDto, LoanSummary> getLoansWithSummary(
      Authentication authentication, Pageable pageable) {
    log.info("Fetching loan details with summary for authenticated user (page {}, size {})",
        pageable.getPageNumber(), pageable.getPageSize());

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);
    log.info("User found (ID: {})", user.getId());

    // Fetch paginated loans
    Page<Loan> loansPage = loanRepository.findByUserId(user.getId(), pageable);
    log.info("Retrieved {} loans (page {} of {}) for user ID: {}",
        loansPage.getNumberOfElements(), loansPage.getNumber() + 1,
        loansPage.getTotalPages(), user.getId());

    // Compute loan summary (aggregates from ALL loans, not just current page)
    BigDecimal totalLoanAmount = loanRepository.sumTotalLoanByUserId(user.getId());
    BigDecimal totalOutstanding = loanRepository.sumOutstandingByUserId(user.getId());
    BigDecimal totalPaid = loanRepository.sumPaidByUserId(user.getId());
    Long activeLoanCount = loanRepository.countActiveByUserId(user.getId());
    Long totalLoanCount = loanRepository.countByUserId(user.getId());

    LoanSummary summary = LoanSummary.builder()
        .totalLoanAmount(totalLoanAmount)
        .totalOutstanding(totalOutstanding)
        .totalPaid(totalPaid)
        .activeLoanCount(activeLoanCount)
        .totalLoanCount(totalLoanCount)
        .build();

    log.info("Loan summary computed - total: {}, outstanding: {}, paid: {}, active: {}, total count: {}",
        totalLoanAmount, totalOutstanding, totalPaid, activeLoanCount, totalLoanCount);

    // Map to DTOs and wrap in PageResponse
    Page<LoanDto> dtoPage = loansPage.map(loanMapper::toDto);
    return PageResponse.of(dtoPage, summary);
  }
}
