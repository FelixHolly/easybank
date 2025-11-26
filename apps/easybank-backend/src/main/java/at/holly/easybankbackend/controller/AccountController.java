package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.AccountDto;
import at.holly.easybankbackend.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Account Controller
 * Handles account-related HTTP endpoints
 * Delegates business logic to AccountService
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {

  private final AccountService accountService;

  /**
   * Get account details for authenticated user
   *
   * @param authentication the authentication object containing JWT token
   * @return the account DTO, or null if no account exists
   */
  @GetMapping("/myAccount")
  public AccountDto getAccountDetails(Authentication authentication) {
    log.info("GET /myAccount - Fetching account details");
    log.debug("Authentication: {}, Authorities: {}",
        authentication.getName(), authentication.getAuthorities());

    return accountService.getAccountForUser(authentication);
  }

}
