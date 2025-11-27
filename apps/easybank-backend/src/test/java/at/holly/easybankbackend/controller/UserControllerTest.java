package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.User;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for UserController.
 * Tests the /api/v1/user endpoint with mock JWT authentication.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("UserController Integration Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static final String TEST_EMAIL = "test.user@example.com";
    private static final String TEST_NAME = "Test User";

    @BeforeEach
    void setUp() {
        // Clean up before each test
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/v1/user - Should return existing user details")
    void shouldReturnExistingUserDetails() throws Exception {
        // Given - Create user in database
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setName(TEST_NAME);
        user.setCreateDt(Date.valueOf(LocalDate.now()));
        userRepository.save(user);

        // When & Then
        mockMvc.perform(get("/api/v1/user")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("email", TEST_EMAIL))
                                .authorities(() -> "ROLE_USER")
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.password").doesNotExist()) // Password should not be exposed
                .andExpect(jsonPath("$.mobileNumber").doesNotExist()); // Mobile should not be exposed
    }

    @Test
    @DisplayName("GET /api/v1/user - Should create new user via JIT provisioning")
    void shouldCreateNewUserViaJitProvisioning() throws Exception {
        // Given - No user in database
        String newUserEmail = "new.user@example.com";
        String givenName = "New";
        String familyName = "User";

        // When & Then
        mockMvc.perform(get("/api/v1/user")
                        .with(jwt()
                                .jwt(jwt -> {
                                    jwt.claim("email", newUserEmail);
                                    jwt.claim("given_name", givenName);
                                    jwt.claim("family_name", familyName);
                                })
                                .authorities(() -> "ROLE_USER")
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newUserEmail))
                .andExpect(jsonPath("$.name").value("New User"));

        // Verify user was created in database
        User createdUser = userRepository.findByEmail(newUserEmail).orElse(null);
        assert createdUser != null;
        assert createdUser.getEmail().equals(newUserEmail);
        assert createdUser.getName().equals("New User");
    }

    @Test
    @DisplayName("GET /api/v1/user - Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/user - Should handle user with only first name")
    void shouldHandleUserWithOnlyFirstName() throws Exception {
        // Given
        String email = "firstname.only@example.com";
        String givenName = "FirstName";

        // When & Then
        mockMvc.perform(get("/api/v1/user")
                        .with(jwt()
                                .jwt(jwt -> {
                                    jwt.claim("email", email);
                                    jwt.claim("given_name", givenName);
                                })
                                .authorities(() -> "ROLE_USER")
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value(givenName));
    }

    @Test
    @DisplayName("GET /api/v1/user - Should fallback to username when no name claims")
    void shouldFallbackToUsernameWhenNoNameClaims() throws Exception {
        // Given
        String email = "username.fallback@example.com";
        String username = "cooluser123";

        // When & Then
        mockMvc.perform(get("/api/v1/user")
                        .with(jwt()
                                .jwt(jwt -> {
                                    jwt.claim("email", email);
                                    jwt.claim("preferred_username", username);
                                })
                                .authorities(() -> "ROLE_USER")
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value(username));
    }

    @Test
    @DisplayName("GET /api/v1/user - Should use email prefix when all name claims are missing")
    void shouldUseEmailPrefixWhenAllNameClaimsMissing() throws Exception {
        // Given
        String email = "emailprefix@example.com";

        // When & Then
        mockMvc.perform(get("/api/v1/user")
                        .with(jwt()
                                .jwt(jwt -> jwt.claim("email", email))
                                .authorities(() -> "ROLE_USER")
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("emailprefix"));
    }
}
