package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.LoanDto;
import at.holly.easybankbackend.dto.LoanMapper;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.model.Loan;
import at.holly.easybankbackend.repository.LoanRepository;
import at.holly.easybankbackend.service.UserProvisioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Loans Controller
 * Handles loan-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
public class LoansController {

  private final LoanRepository loanRepository;
  private final UserProvisioningService userProvisioningService;
  private final LoanMapper loanMapper;

  /**
   * Get loan details for a user
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @return list of loan DTOs ordered by start date descending
   */
  @GetMapping("/myLoans")
  public List<LoanDto> getLoansDetails(Authentication authentication) {
    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);

    List<Loan> loans = loanRepository.findByUserIdOrderByStartDtDesc(user.getId());
    return loanMapper.toDtoList(loans);
  }

}
