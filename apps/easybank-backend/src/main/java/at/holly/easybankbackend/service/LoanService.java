package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.LoanDto;
import at.holly.easybankbackend.dto.LoanMapper;
import at.holly.easybankbackend.model.Loan;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
   * Get loan details for authenticated user
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @return list of loan DTOs ordered by start date descending
   */
  @Transactional(readOnly = true)
  public List<LoanDto> getLoansForUser(Authentication authentication) {
    log.info("Fetching loan details for authenticated user");

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);
    log.info("User found: {} (ID: {})", user.getEmail(), user.getId());

    // Fetch loans
    List<Loan> loans = loanRepository.findByUserIdOrderByStartDtDesc(user.getId());
    log.info("Retrieved {} loans for user: {}", loans.size(), user.getEmail());

    return loanMapper.toDtoList(loans);
  }
}
