package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.AccountDto;
import at.holly.easybankbackend.dto.AccountMapper;
import at.holly.easybankbackend.model.Account;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.AccountRepository;
import at.holly.easybankbackend.service.UserProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Account Controller
 * Handles account-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {

  private final AccountRepository accountRepository;
  private final UserProvisioningService userProvisioningService;
  private final AccountMapper accountMapper;

  /**
   * Get account details for a user
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @return the account DTO, or null if no account exists
   */
  @GetMapping("/myAccount")
  public AccountDto getAccountDetails(Authentication authentication) {
    log.info("GET /myAccount - Fetching account details");
    log.debug("Authentication: {}, Authorities: {}",
      authentication.getName(), authentication.getAuthorities());

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);

    log.info("User found: {} (ID: {})", user.getEmail(), user.getId());
    Account account = accountRepository.findByUserId(user.getId());
    log.info("Account retrieved successfully for user: {}", user.getEmail());

    return accountMapper.toDto(account);
  }

}
