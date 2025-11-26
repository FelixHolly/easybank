package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.enums.AccountType;
import at.holly.easybankbackend.model.Account;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.AccountRepository;
import at.holly.easybankbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AccountController.
 * Tests the /myAccount endpoint with mock JWT authentication.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AccountController Integration Tests")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private User testUser;

  private static final String TEST_EMAIL = "account.test@example.com";
    private static final long TEST_ACCOUNT_NUMBER = 10000001L;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        accountRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setEmail(TEST_EMAIL);
        testUser.setName("Account Test User");
        testUser.setCreateDt(Date.valueOf(LocalDate.now()));
        testUser = userRepository.save(testUser);

        // Create test account
      Account testAccount = new Account();
        testAccount.setAccountNumber(TEST_ACCOUNT_NUMBER);
        testAccount.setUserId(testUser.getId());
        testAccount.setAccountType(AccountType.SAVINGS);
        testAccount.setBranchAddress("123 Main St, Test City");
        testAccount.setCreateDt(Date.valueOf(LocalDate.now()));
        accountRepository.save(testAccount);
    }

    @Test
    @DisplayName("GET /myAccount - Should return user's account")
    void shouldReturnUserAccount() throws Exception {
        // When & Then
        mockMvc.perform(get("/myAccount")
                        .with(csrf())
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("email", TEST_EMAIL))
                                .authorities(() -> "ROLE_USER")
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(TEST_ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.branchAddress").value("123 Main St, Test City"))
                .andExpect(jsonPath("$.userId").value(testUser.getId()));
    }

    @Test
    @DisplayName("GET /myAccount - Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/myAccount")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /myAccount - Should return 403 when user does not have ROLE_USER")
    void shouldReturn403WhenNoRoleUser() throws Exception {
        // When & Then
        mockMvc.perform(get("/myAccount")
                        .with(csrf())
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("email", TEST_EMAIL))
                                .authorities(() -> "ROLE_ADMIN") // Wrong role
                        ))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /myAccount - Should create user via JIT when first accessing")
    void shouldCreateUserViaJitWhenFirstAccessing() throws Exception {
        // Given - New user email (no user exists yet in database)
        String newUserEmail = "new.jit.user@example.com";

        // When - User accesses endpoint for the first time
        // JIT provisioning will create the user automatically
        mockMvc.perform(get("/myAccount")
                        .with(csrf())
                        .with(jwt()
                                .jwt(jwt -> {
                                    jwt.claim("email", newUserEmail);
                                    jwt.claim("given_name", "JIT");
                                    jwt.claim("family_name", "User");
                                })
                                .authorities(() -> "ROLE_USER")
                        ))
                .andExpect(status().isOk())
                // User was created via JIT but has no account yet
                .andExpect(content().string(""));

        // Then - Verify user was created
        User createdUser = userRepository.findByEmail(newUserEmail).orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(newUserEmail);
        assertThat(createdUser.getName()).isEqualTo("JIT User");
    }

    @Test
    @DisplayName("GET /myAccount - Should return null when user has no account")
    void shouldReturnNullWhenUserHasNoAccount() throws Exception {
        // Given - User without account
        String emailNoAccount = "no.account@example.com";

        User userNoAccount = new User();
        userNoAccount.setEmail(emailNoAccount);
        userNoAccount.setName("No Account User");
        userNoAccount.setCreateDt(Date.valueOf(LocalDate.now()));
        userRepository.save(userNoAccount);

        // When & Then
        mockMvc.perform(get("/myAccount")
                        .with(csrf())
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("email", emailNoAccount))
                                .authorities(() -> "ROLE_USER")
                        ))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}
