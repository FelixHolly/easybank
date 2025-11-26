package at.holly.easybankbackend.service;

import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserProvisioningService.
 * Tests the Just-In-Time (JIT) user provisioning logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserProvisioningService Tests")
class UserProvisioningServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserProvisioningService userProvisioningService;

    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";

    @BeforeEach
    void setUp() {
        // This method is intentionally empty
        // Mocks are configured per-test as needed
    }

    @Test
    @DisplayName("Should return existing user when user already exists")
    void shouldReturnExistingUser() {
        // Given
        User existingUser = createTestUser();
        when(jwtService.extractEmail(authentication)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));

        // When
        User result = userProvisioningService.getOrCreateUser(authentication);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getName()).isEqualTo("John Doe");

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should create new user when user does not exist")
    void shouldCreateNewUserWhenNotExists() {
        // Given
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwtService.extractEmail(authentication)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        when(jwt.getClaimAsString("email")).thenReturn(TEST_EMAIL);
        when(jwt.getClaimAsString("given_name")).thenReturn(TEST_FIRST_NAME);
        when(jwt.getClaimAsString("family_name")).thenReturn(TEST_LAST_NAME);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("johndoe");

        User savedUser = createTestUser();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userProvisioningService.getOrCreateUser(authentication);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(capturedUser.getName()).isEqualTo("John Doe");
        assertThat(capturedUser.getCreateDt()).isEqualTo(Date.valueOf(LocalDate.now()));
    }

    @Test
    @DisplayName("Should handle user with first name only")
    void shouldHandleUserWithFirstNameOnly() {
        // Given
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwtService.extractEmail(authentication)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        when(jwt.getClaimAsString("email")).thenReturn(TEST_EMAIL);
        when(jwt.getClaimAsString("given_name")).thenReturn(TEST_FIRST_NAME);
        when(jwt.getClaimAsString("family_name")).thenReturn(null);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("johndoe");

        User savedUser = createTestUser();
        savedUser.setName(TEST_FIRST_NAME);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userProvisioningService.getOrCreateUser(authentication);

        // Then
        assertThat(result).isNotNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getName()).isEqualTo(TEST_FIRST_NAME);
    }

    @Test
    @DisplayName("Should fallback to username when no name claims present")
    void shouldFallbackToUsernameWhenNoNames() {
        // Given
        when(authentication.getPrincipal()).thenReturn(jwt);
        String username = "johndoe";
        when(jwtService.extractEmail(authentication)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        when(jwt.getClaimAsString("email")).thenReturn(TEST_EMAIL);
        when(jwt.getClaimAsString("given_name")).thenReturn(null);
        when(jwt.getClaimAsString("family_name")).thenReturn(null);
        when(jwt.getClaimAsString("preferred_username")).thenReturn(username);

        User savedUser = createTestUser();
        savedUser.setName(username);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userProvisioningService.getOrCreateUser(authentication);

        // Then
        assertThat(result).isNotNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getName()).isEqualTo(username);
    }

    @Test
    @DisplayName("Should fallback to email prefix when all name claims are null")
    void shouldFallbackToEmailPrefixWhenAllNamesNull() {
        // Given
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwtService.extractEmail(authentication)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        when(jwt.getClaimAsString("email")).thenReturn(TEST_EMAIL);
        when(jwt.getClaimAsString("given_name")).thenReturn(null);
        when(jwt.getClaimAsString("family_name")).thenReturn(null);
        when(jwt.getClaimAsString("preferred_username")).thenReturn(null);

        User savedUser = createTestUser();
        savedUser.setName("john.doe");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userProvisioningService.getOrCreateUser(authentication);

        // Then
        assertThat(result).isNotNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getName()).isEqualTo("john.doe");
    }

    /**
     * Helper method to create a test user
     */
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail(TEST_EMAIL);
        user.setName("John Doe");
        user.setCreateDt(Date.valueOf(LocalDate.now()));
        return user;
    }
}
