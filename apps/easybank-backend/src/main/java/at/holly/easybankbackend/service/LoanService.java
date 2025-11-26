package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.LoanDto;
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
   * Get loan details for authenticated user (paginated)
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @param pageable pagination and sorting parameters
   * @return page of loan DTOs
   */
  @Transactional(readOnly = true)
  public Page<LoanDto> getLoansForUser(Authentication authentication, Pageable pageable) {
    log.info("Fetching loan details for authenticated user (page {}, size {})",
        pageable.getPageNumber(), pageable.getPageSize());

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);
    log.info("User found (ID: {})", user.getId());

    // Fetch loans with pagination
    Page<Loan> loans = loanRepository.findByUserId(user.getId(), pageable);
    log.info("Retrieved {} loans (page {} of {}) for user ID: {}",
        loans.getNumberOfElements(), loans.getNumber() + 1, loans.getTotalPages(), user.getId());

    return loans.map(loanMapper::toDto);
  }
}
