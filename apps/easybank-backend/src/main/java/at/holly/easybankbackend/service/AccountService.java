package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.AccountDto;
import at.holly.easybankbackend.dto.AccountMapper;
import at.holly.easybankbackend.model.Account;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Account Service
 * Handles business logic for account operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserProvisioningService userProvisioningService;
  private final AccountMapper accountMapper;

  /**
   * Get account details for authenticated user
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @return the account DTO, or null if no account exists
   */
  @Transactional(readOnly = true)
  public AccountDto getAccountForUser(Authentication authentication) {
    log.info("Fetching account details for authenticated user");

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);
    log.info("User found: {} (ID: {})", user.getEmail(), user.getId());

    // Fetch account
    Account account = accountRepository.findByUserId(user.getId());

    if (account != null) {
      log.info("Account retrieved successfully for user: {}", user.getEmail());
    } else {
      log.info("No account found for user: {}", user.getEmail());
    }

    return accountMapper.toDto(account);
  }
}
