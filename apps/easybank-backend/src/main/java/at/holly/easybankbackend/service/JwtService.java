package at.holly.easybankbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * JWT Service
 * Handles extraction and parsing of JWT token claims from Keycloak
 */
@Service
@Slf4j
public class JwtService {

  /**
   * Extract email from Keycloak JWT token
   *
   * @param authentication the authentication object containing JWT token
   * @return the email address from the token
   * @throws RuntimeException if email claim is not found or authentication is invalid
   */
  public String extractEmail(Authentication authentication) {
    log.debug("Extracting email from JWT token");
    Jwt jwt = extractJwt(authentication);
    String email = jwt.getClaimAsString("email");

    if (email == null || email.isEmpty()) {
      log.error("Email claim not found in JWT token. Available claims: {}", jwt.getClaims().keySet());
      throw new RuntimeException("Email claim not found in JWT token");
    }

    log.info("Successfully extracted email: {}", email);
    return email;
  }

  /**
   * Extract username (preferred_username) from Keycloak JWT token
   *
   * @param authentication the authentication object containing JWT token
   * @return the preferred username from the token
   * @throws RuntimeException if authentication is invalid
   */
  public String extractUsername(Authentication authentication) {
    Jwt jwt = extractJwt(authentication);
    return jwt.getClaimAsString("preferred_username");
  }

  /**
   * Extract subject (sub) from JWT token
   * This is typically the user's unique identifier in Keycloak
   *
   * @param authentication the authentication object containing JWT token
   * @return the subject (user ID) from the token
   * @throws RuntimeException if authentication is invalid
   */
  public String extractSubject(Authentication authentication) {
    Jwt jwt = extractJwt(authentication);
    return jwt.getSubject();
  }

  /**
   * Extract given name (first name) from JWT token
   *
   * @param authentication the authentication object containing JWT token
   * @return the given name from the token, or null if not present
   * @throws RuntimeException if authentication is invalid
   */
  public String extractGivenName(Authentication authentication) {
    Jwt jwt = extractJwt(authentication);
    return jwt.getClaimAsString("given_name");
  }

  /**
   * Extract family name (last name) from JWT token
   *
   * @param authentication the authentication object containing JWT token
   * @return the family name from the token, or null if not present
   * @throws RuntimeException if authentication is invalid
   */
  public String extractFamilyName(Authentication authentication) {
    Jwt jwt = extractJwt(authentication);
    return jwt.getClaimAsString("family_name");
  }

  /**
   * Get the full name from JWT token (combines given_name and family_name)
   *
   * @param authentication the authentication object containing JWT token
   * @return the full name, or username if names are not available
   * @throws RuntimeException if authentication is invalid
   */
  public String extractFullName(Authentication authentication) {
    String givenName = extractGivenName(authentication);
    String familyName = extractFamilyName(authentication);

    if (givenName != null && familyName != null) {
      return givenName + " " + familyName;
    } else if (givenName != null) {
      return givenName;
    } else if (familyName != null) {
      return familyName;
    } else {
      return extractUsername(authentication);
    }
  }

  /**
   * Extract a custom claim from JWT token
   *
   * @param authentication the authentication object containing JWT token
   * @param claimName the name of the claim to extract
   * @return the claim value as String, or null if not present
   * @throws RuntimeException if authentication is invalid
   */
  public String extractClaim(Authentication authentication, String claimName) {
    Jwt jwt = extractJwt(authentication);
    return jwt.getClaimAsString(claimName);
  }

  /**
   * Extract JWT token from Authentication object
   *
   * @param authentication the authentication object
   * @return the JWT token
   * @throws RuntimeException if authentication is not a JwtAuthenticationToken
   */
  private Jwt extractJwt(Authentication authentication) {
    log.debug("Extracting JWT from authentication object. Type: {}",
      authentication != null ? authentication.getClass().getSimpleName() : "null");

    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();
      log.debug("JWT token extracted. Subject: {}, Issuer: {}, Expiration: {}",
        jwt.getSubject(), jwt.getIssuer(), jwt.getExpiresAt());
      log.debug("JWT Claims: {}", jwt.getClaims().keySet());
      return jwt;
    }

    log.error("Invalid authentication type. Expected JwtAuthenticationToken but got: {}",
      authentication != null ? authentication.getClass().getSimpleName() : "null");
    throw new RuntimeException("Invalid authentication type. Expected JWT token but got: " +
      (authentication != null ? authentication.getClass().getSimpleName() : "null"));
  }

  /**
   * Get the raw JWT token object for advanced operations
   *
   * @param authentication the authentication object containing JWT token
   * @return the Jwt token object
   * @throws RuntimeException if authentication is invalid
   */
  public Jwt getJwt(Authentication authentication) {
    return extractJwt(authentication);
  }
}
