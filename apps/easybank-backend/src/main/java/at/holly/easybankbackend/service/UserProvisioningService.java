package at.holly.easybankbackend.service;

import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

/**
 * User Provisioning Service
 * Handles Just-In-Time (JIT) user provisioning from Keycloak to the application database.
 * When a user registers in Keycloak and makes their first API call, this service
 * automatically creates a corresponding User record in the database using
 * information from the JWT token.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProvisioningService {

  private final UserRepository userRepository;
  private final JwtService jwtService;

  /**
   * Get or create a user from JWT authentication.
   * If the user doesn't exist in the database, create them automatically.
   *
   * @param authentication Spring Security Authentication object containing JWT
   * @return User entity (existing or newly created)
   */
  @Transactional
  public User getOrCreateUser(Authentication authentication) {
    String email = jwtService.extractEmail(authentication);

    log.debug("Looking up user with email: {}", email);

    return userRepository.findByEmail(email)
      .orElseGet(() -> {
        log.info("User not found with email: {}. Creating new user from JWT claims.", email);
        return createUserFromJwt(authentication);
      });
  }

  /**
   * Create a new user from JWT token claims.
   * Extracts user information from Keycloak JWT token and creates a User entity.
   *
   * @param authentication Spring Security Authentication containing JWT
   * @return Newly created User entity
   */
  private User createUserFromJwt(Authentication authentication) {
    Jwt jwt = (Jwt) authentication.getPrincipal();

    // Extract user information from JWT claims
    String email = jwt.getClaimAsString("email");
    String firstName = jwt.getClaimAsString("given_name");
    String lastName = jwt.getClaimAsString("family_name");
    String preferredUsername = jwt.getClaimAsString("preferred_username");

    // Build full name from available claims
    String name = buildFullName(firstName, lastName, preferredUsername, email);

    // Create new user
    User user = new User();
    user.setEmail(email);
    user.setName(name);
    user.setCreateDt(Date.valueOf(LocalDate.now()));

    // Note: mobileNumber not available from Keycloak by default
    // Can be collected later via profile update or custom Keycloak attributes

    User savedUser = userRepository.save(user);
    log.info("Successfully created new user: {} (ID: {})", email, savedUser.getId());

    return savedUser;
  }

  /**
   * Build full name from available JWT claims.
   * Falls back gracefully if claims are missing.
   *
   * @param firstName User's first name
   * @param lastName User's last name
   * @param username Keycloak username
   * @param email User's email
   * @return Constructed full name
   */
  private String buildFullName(String firstName, String lastName, String username, String email) {
    if (firstName != null && lastName != null) {
      return firstName + " " + lastName;
    } else if (firstName != null) {
      return firstName;
    } else if (username != null) {
      return username;
    } else {
      // Fallback: use email prefix
      return email.split("@")[0];
    }
  }
}
