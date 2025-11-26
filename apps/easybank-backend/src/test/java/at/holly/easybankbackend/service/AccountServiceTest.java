package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.AccountDto;
import at.holly.easybankbackend.mapper.AccountMapper;
import at.holly.easybankbackend.model.Account;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Tests")
class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private UserProvisioningService userProvisioningService;

  @Mock
  private AccountMapper accountMapper;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private AccountService accountService;

  @Test
  @DisplayName("Should get account for user successfully")
  void shouldGetAccountForUser() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setEmail("john.doe@example.com");
    user.setName("John Doe");

    Account account = new Account();
    account.setAccountNumber(123456789L);
    account.setUserId(1L);
    account.setAccountType("Savings");
    account.setBranchAddress("Main Branch");
    account.setCreateDt(new Date(System.currentTimeMillis()));

    AccountDto accountDto = AccountDto.builder()
        .accountNumber(123456789L)
        .userId(1L)
        .accountType("Savings")
        .branchAddress("Main Branch")
        .build();

    when(userProvisioningService.getOrCreateUser(authentication)).thenReturn(user);
    when(accountRepository.findByUserId(1L)).thenReturn(account);
    when(accountMapper.toDto(account)).thenReturn(accountDto);

    // When
    AccountDto result = accountService.getAccountForUser(authentication);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getAccountNumber()).isEqualTo(123456789L);
    assertThat(result.getUserId()).isEqualTo(1L);
    assertThat(result.getAccountType()).isEqualTo("Savings");

    verify(userProvisioningService).getOrCreateUser(authentication);
    verify(accountRepository).findByUserId(1L);
    verify(accountMapper).toDto(account);
  }

  @Test
  @DisplayName("Should return null when no account exists for user")
  void shouldReturnNullWhenNoAccountExists() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setEmail("john.doe@example.com");
    user.setName("John Doe");

    when(userProvisioningService.getOrCreateUser(authentication)).thenReturn(user);
    when(accountRepository.findByUserId(1L)).thenReturn(null);
    when(accountMapper.toDto(null)).thenReturn(null);

    // When
    AccountDto result = accountService.getAccountForUser(authentication);

    // Then
    assertThat(result).isNull();

    verify(userProvisioningService).getOrCreateUser(authentication);
    verify(accountRepository).findByUserId(1L);
    verify(accountMapper).toDto(null);
  }

  @Test
  @DisplayName("Should trigger JIT provisioning when user doesn't exist")
  void shouldTriggerJitProvisioning() {
    // Given
    User newUser = new User();
    newUser.setId(2L);
    newUser.setEmail("new.user@example.com");
    newUser.setName("New User");

    Account account = new Account();
    account.setAccountNumber(987654321L);
    account.setUserId(2L);

    AccountDto accountDto = AccountDto.builder()
        .accountNumber(987654321L)
        .userId(2L)
        .build();

    when(userProvisioningService.getOrCreateUser(authentication)).thenReturn(newUser);
    when(accountRepository.findByUserId(2L)).thenReturn(account);
    when(accountMapper.toDto(account)).thenReturn(accountDto);

    // When
    AccountDto result = accountService.getAccountForUser(authentication);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getUserId()).isEqualTo(2L);

    verify(userProvisioningService).getOrCreateUser(authentication);
    verify(accountRepository).findByUserId(2L);
  }
}
